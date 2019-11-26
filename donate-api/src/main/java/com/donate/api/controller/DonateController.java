package com.donate.api.controller;

import com.donate.api.dto.RecordDTO;
import com.donate.common.model.Pagination;
import com.donate.common.model.ServiceResult;
import com.donate.common.model.ServiceResultT;
import com.donate.common.properties.WxMpConfig;
import com.donate.dao.entity.Donate;
import com.donate.dao.entity.User;
import com.donate.service.donate.DonateService;
import com.donate.service.donate.UserService;
import com.sun.prism.impl.Disposer;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;


@RequestMapping(value = "donate")
@Controller
@Api(value = "donate", description = "捐赠接口", produces = MediaType.APPLICATION_JSON_VALUE)
public class DonateController {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    DonateService donateService;

    @Autowired
    UserService userService;

    @Autowired
    WxMpConfig wxMpConfig;

    /**
     * 保存捐款记录2001
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "donate", method = RequestMethod.POST)
    @ApiOperation(value = "2001保存捐款记录", notes = "2001", produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceResult donate(
            @ApiParam(value = "openId=用户在对应公众号下唯一id", required = true, name = "openId")
                    String openId,
            @ApiParam(value = "amount=捐款金额", required = true, name = "amount")
                    Double amount,
            @ApiParam(value = "name=捐款人姓名", name = "name")
                    String name,
            @ApiParam(value = "mobile=捐款人手机号码", name = "mobile")
                    String mobile,
            @ApiParam(value = "schoolClass=班期", name = "schoolClass")
                    String schoolClass,
            @ApiParam(value = "period=期数", name = "period")
                    String period) throws Exception {

        ServiceResult result = new ServiceResult();
        User userBO = userService.getUserByOpenId(openId);

        if (amount == null) {
            result.failed("捐赠金额不能为空");
        } else if (amount < 0.01) {
            result.failed("捐赠金额不能小于0.01");
        } else if (amount > 200000) {
            result.failed("捐赠金额已超出限额，请再输入");
        } else if (name != null && name.length() >= 25) {
            result.failed("捐赠姓名，长度超长了");
        } else if (name != null && !removeFourChar(name).equals(name)) {
            result.failed("姓名可能包含非法字符，请重新输入。");
        } else if (StringUtils.isBlank(openId)) {
            result.failed("openId不能为空");
        } else if (userBO == null) {

            result.failed("获取微信用户失败");
        } else {
            Donate donateBO = new Donate();
            Map<String, Object> map = new HashMap<String, Object>();

            String donateId = UUID.randomUUID().toString();
            donateBO.setCreateTime(new Date());
            donateBO.setAmount(new BigDecimal(amount));
            donateBO.setMobile(mobile);
            donateBO.setSchoolClass(schoolClass);
            donateBO.setPeriod(period);
            donateBO.setUserName(name);
            donateBO.setId(donateId);

            donateBO.setNickName(userBO.getNickName());
            donateBO.setHeadImgUrl(userBO.getHeadImgUrl());
            donateBO.setOpenId(openId);

            result = donateService.addDonate(donateBO);
            if (result.getIsSuccess()) {

                map.put("donateId", donateId);
                result.setDicData(map);
            }
        }

        return result;
    }

    /**
     * 获取单个捐赠详情
     *
     * @param donateId
     * @param openId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getDonateInfo", method = RequestMethod.POST)
    @ApiOperation(value = "2002通过订单号查询订单信息（含openId）", notes = "2002")
    public ServiceResultT<Donate> getDonateInfo(
            @ApiParam(value = "订单Id", required = true, name = "donateId") String donateId,
            @ApiParam(value = "微信open_id", required = true, name = "openId") String openId
    ) throws Exception {

        ServiceResultT<Donate> result = new ServiceResultT<Donate>();
        if (StringUtils.isBlank(donateId)) {

            result.failed("捐赠id不能为空");
        } else if (StringUtils.isBlank(openId)) {
            result.failed("openId不能为空");
        } else {

			Donate donateDTO = new Donate();

            Pagination queryPgn = new Pagination();
            Map<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("openId", openId);
            conditions.put("donateId", donateId);
            queryPgn.setConditions(conditions);
            List<Donate> boList = donateService.selectDonateByPagin(queryPgn);
            if (!boList.isEmpty()) {

                donateDTO.setAmountStr(boList.get(0).getAmountStr());
                donateDTO.setAmount(boList.get(0).getAmount());
                donateDTO.setPeriod(boList.get(0).getPeriod());
                donateDTO.setSchoolClass(boList.get(0).getSchoolClass());
                donateDTO.setHeadImgUrl(changeImgUrl(boList.get(0).getHeadImgUrl()));
                try {
                    if (StringUtils.isBlank(boList.get(0).getUserName())) {

                        donateDTO.setNickName(URLDecoder.decode(boList.get(0).getNickName(), "utf-8"));
                    } else {
                        donateDTO.setNickName(boList.get(0).getUserName());
                    }
                } catch (Exception e) {
                }
            }
            result.setDicData(donateDTO);
        }

        return result;
    }


    /**
     * 获取单个捐赠详情,不包含openId
     *
     * @param donateId
     * @return
     * @author ucs_zhangfengjin
     */
    @ResponseBody
    @RequestMapping(value = "getDonateDetail", method = RequestMethod.POST)
    @ApiOperation(value = "2004通过订单号查询订单信息（不含openId）", notes = "2004")
    public ServiceResultT<Donate> getDonateDetail(
            @ApiParam(value = "订单Id", required = true, name = "donateId")
                    String donateId) throws Exception {

        ServiceResultT<Donate> result = new ServiceResultT<Donate>();

        if (StringUtils.isBlank(donateId)) {
            result.failed("捐赠id不能为空");
            return result;
        }

		Donate donateDTO = new Donate();

		Donate donateBO = donateService.selectDonationDetail(donateId);

        if (donateBO == null) {
            result.failed("没有查询到正确的数据");
            return result;
        }

        donateDTO.setAmountStr(donateBO.getAmountStr());
        donateDTO.setAmount(donateBO.getAmount());
        donateDTO.setSchoolClass(donateBO.getSchoolClass());
        donateDTO.setPeriod(donateBO.getPeriod());
        donateDTO.setHeadImgUrl(changeImgUrl(donateBO.getHeadImgUrl()));
        if (!StringUtils.isBlank(donateBO.getUserName())) {
            donateDTO.setUserName(donateBO.getUserName());
        } else {
            donateDTO.setNickName(URLDecoder.decode(donateBO.getNickName(), "utf-8"));
        }
        result.succeed("获取成功");
        result.setDicData(donateDTO);
        return result;
    }


    /**
     * 获取全部捐赠总额
     *
     * @param time
     * @return
     * @author ucs_zhangfengjin
     */
    @ResponseBody
    @RequestMapping(value = "getTotalAmount", method = RequestMethod.POST)
    @ApiOperation(value = "2005查询所有订单金额总额", notes = "2005")
    public ServiceResult getTotalAmount(
            @ApiParam(value = "上拉加载数据时传入的时间点", required = false, name = "time")
                    String time
    ) throws Exception {
        ServiceResult result = new ServiceResult();

        Pagination queryPgn = new Pagination();
        Map<String, Object> conditions = new HashMap<String, Object>();

        if (!StringUtils.isBlank(time))
            conditions.put("time", time);

        queryPgn.setConditions(conditions);
        try {
            BigDecimal totalAmount = donateService.selectTotalAmount(queryPgn);
            if (totalAmount == null) {
                totalAmount = new BigDecimal("0.00");
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("totalAmount", totalAmount);
            result.succeed("获取成功");
            result.setDicData(map);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.failed("获取失败");
        }


        return result;
    }


    /**
     * 获取个人捐赠总额
     *
     * @param openId
     * @param time
     * @return
     * @author ucs_zhangfengjin
     */
    @ResponseBody
    @RequestMapping(value = "getMyTotalAmount", method = RequestMethod.POST)
    @ApiOperation(value = "2007查询个人订单金额总额", notes = "2007")
    public ServiceResult getMyTotalAmount(
            @ApiParam(value = "openId确定用户", required = false, name = "openId") String openId,
            @ApiParam(value = "上拉加载数据时传入的时间点", required = false, name = "time") String time
    ) throws Exception {
        ServiceResult result = new ServiceResult();
        try {

            Pagination queryPgn = new Pagination();
            Map<String, Object> conditions = new HashMap<String, Object>();

            if (StringUtils.isBlank(openId))
                return result.failed("获取个人捐赠总额失败，没有获取到正确的用户");
            if (!StringUtils.isBlank(time))
                conditions.put("time", time);

            conditions.put("openId", openId);

            queryPgn.setConditions(conditions);

            BigDecimal myTotalAmount = donateService.selectMyTotalAmount(queryPgn);
            if (myTotalAmount == null) {
                myTotalAmount = new BigDecimal("0.00");
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("myTotalAmount", myTotalAmount);
            result.succeed("获取成功");
            result.setDicData(map);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.failed("获取失败");
        }

        return result;
    }


    /**
     * 获取所有捐赠记录，分页查询
     *
     * @param pageIndex
     * @param pageSize
     * @param time
     * @return
     * @author ucs_zhangfengjin
     */
    @ResponseBody
    @RequestMapping(value = "getAllDonateList", method = RequestMethod.POST)
    @ApiOperation(value = "2006查询所有订单金额列表，分页查询", notes = "2006")
    public ServiceResult getAllDonateList(
            @ApiParam(value = "加载是第几页的数据", required = false, name = "pageIndex") Integer pageIndex,
            @ApiParam(value = "分页查询，每页加载多少条数据", required = false, name = "pageSize") Integer pageSize,
            @ApiParam(value = "上拉加载数据时传入的时间点", required = false, name = "time") String time,
            @ApiParam(value = "确定上下拉。1：表示刷新（下拉）；0：表示上拉加载", required = false, name = "type") Integer type
    ) throws Exception {
        ServiceResult result = new ServiceResult();

        Pagination queryPgn = new Pagination();
        Map<String, Object> conditions = new HashMap<String, Object>();

        if (!StringUtils.isBlank(time))
            conditions.put("time", time);
        if (type == null)
            conditions.put("type", 0);
        else
            conditions.put("type", type);

        queryPgn.setCurPageNO(pageIndex);
        queryPgn.setPageSize(pageSize);
        queryPgn.toBePage();
        queryPgn.setConditions(conditions);

        try {
            List<Donate> boList = donateService.selectAllDonationList(queryPgn);
            List<RecordDTO> recordList = new ArrayList<RecordDTO>();
            for (Donate bo : boList) {
                RecordDTO rVO = new RecordDTO();
                if (!StringUtils.isBlank(bo.getUserName())) {
                    rVO.setName(bo.getUserName());
                } else {
                    rVO.setName(URLDecoder.decode(bo.getNickName(), "utf-8"));
                }
                rVO.setAmount(bo.getAmount());
                rVO.setAmountStr(bo.getAmountStr());
                rVO.setCreateTime(bo.getCreateTime());
                rVO.setHeadImgUrl(changeImgUrl(bo.getHeadImgUrl()));
                recordList.add(rVO);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recordList", recordList);
            result.succeed("获取成功");
            result.setDicData(map);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.failed("获取失败");
        }

        return result;
    }

    /**
     * 获取个人捐赠记录，分页查询
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @author ucs_zhangfengjin
     */
    @ResponseBody
    @RequestMapping(value = "getMyDonationList", method = RequestMethod.POST)
    @ApiOperation(value = "2008查询个人订单金额列表，分页查询", notes = "2008")
    public ServiceResult getMyDonationList(
            @ApiParam(value = "加载是第几页的数据", required = false, name = "pageIndex")
                    Integer pageIndex,
            @ApiParam(value = "分页查询，每页加载多少条数据", required = false, name = "pageSize")
                    Integer pageSize,
            @ApiParam(value = "上拉加载数据时传入的时间点", required = false, name = "time")
                    String time,
            @ApiParam(value = "确定上下拉。1：表示刷新（下拉）；0：表示上拉加载", required = false, name = "type")
                    Integer type,
            @ApiParam(value = "openId确定用户", required = true, name = "openId")
                    String openId
    ) throws Exception {
        ServiceResult result = new ServiceResult();

        Pagination queryPgn = new Pagination();
        Map<String, Object> conditions = new HashMap<String, Object>();

        if (StringUtils.isBlank(openId)) {
            result.failed("获取失败");
            return result;
        }
        conditions.put("openId", openId);

        if (!StringUtils.isBlank(time))
            conditions.put("time", time);
        if (type == null)
            conditions.put("type", 0);
        else
            conditions.put("type", type);

        queryPgn.setCurPageNO(pageIndex);
        queryPgn.setPageSize(pageSize);
        queryPgn.toBePage();
        queryPgn.setConditions(conditions);

        try {
            List<Donate> boList = donateService.selectMyDonationList(queryPgn);
            List<RecordDTO> recordList = new ArrayList<RecordDTO>();
            for (Donate bo : boList) {
                RecordDTO rVO = new RecordDTO();
                if (!StringUtils.isBlank(bo.getUserName())) {
                    rVO.setName(bo.getUserName());
                } else {
                    rVO.setName(URLDecoder.decode(bo.getNickName(), "utf-8"));
                }
                rVO.setAmount(bo.getAmount());
                rVO.setAmountStr(bo.getAmountStr());
                rVO.setCreateTime(bo.getCreateTime());
                //rVO.setHeadImgUrl(changeImgUrl(bo.getHeadImgUrl()));
                rVO.setProjectName("公益募捐");
                recordList.add(rVO);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("recordList", recordList);
            result.succeed("获取成功");
            result.setDicData(map);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.failed("获取失败");
        }

        return result;
    }

    public String changeImgUrl(String imgUrl) {

        String localHostStr = wxMpConfig.getWxCpDomain().endsWith("/") ?
                wxMpConfig.getWxCpDomain() + "wximg/" + imgUrl :
                wxMpConfig.getWxCpDomain() + "/wximg/" + imgUrl;

        return localHostStr;
    }

    public static String removeFourChar(String content) {
        byte[] conbyte = content.getBytes();
        for (int i = 0; i < conbyte.length; i++) {
            if ((conbyte[i] & 0xF8) == 0xF0) {
                for (int j = 0; j < 4; j++) {
                    conbyte[i + j] = 0x30;
                }

                i += 3;
            }
        }

        content = new String(conbyte);
        return content.replaceAll("0000", "");

    }
}
