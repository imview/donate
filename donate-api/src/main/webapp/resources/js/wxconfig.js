wxCofig = {};
wxShareData = {};

function settingWxConfig(ajaxUrl)
{
//$(function () {
    $.ajax({
        async: true,
        type: "get",
        data: "url=" + encodeURIComponent(location.href.split('#')[0]),      
        url: ajaxUrl,	// "../system/getJsapiSignature",
        success: function (data) {
        	
        	if(data.isSuccess)
        	{       
        		 wxCofig.appId = data.dicData.appId;
                 wxCofig.timestamp = data.dicData.timestamp; //jsConfig.Timestamp;
                 wxCofig.nonceStr = data.dicData.nonceStr; //.jsConfig.Noncestr;
                 wxCofig.signature = data.dicData.signature; //.jsConfig.Signdata;
                 wx.config({
                     debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                     appId: wxCofig.appId, // 必填，公众号的唯一标识
                     timestamp: wxCofig.timestamp, // 必填，生成签名的时间戳
                     nonceStr: wxCofig.nonceStr, // 必填，生成签名的随机串
                     signature: wxCofig.signature, // 必填，签名，见附录1
                     jsApiList: ['openLocation', 'getLocation', 'hideMenuItems','hideAllNonBaseMenuItem','hideOptionMenu'] //'onMenuShareTimeline', 'onMenuShareAppMessage', 'onMenuShareQQ', 'onMenuShareWeibo', 'hideMenuItems'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                 }); 
	
	            //微信分享config
	            wx.ready(function () {
	                // 要隐藏的菜单项，所有menu项见附录3
	                wx.hideMenuItems({
	                    menuList: [
	                    	'menuItem:share:appMessage',	// 发送给朋友
	                    	'menuItem:share:timeline',		// 分享到朋友圈
	                    	'menuItem:share:qq', 		// QQ
	                    	'menuItem:share:weiboApp',  // Weibo
	                    	'menuItem:favorite',      	// 收藏
	                    	'menuItem:share:facebook',  // facebook
	                    	'menuItem:share:QZone',  	//  QQ 空间	                    	

	                    	'menuItem:exposeArticle',	// 举报
	                    	'menuItem:setFont',			// 调整字体	                    	
	                    	'menuItem:editTag',			// 编辑标签
	                    	'menuItem:delete',			// 删除
	                    	'menuItem:readMode',  		// 阅读模式
	                    	'menuItem:openWithQQBrowser', // 在QQ浏览器中打开
	                    	'menuItem:openWithSafari',   // 在Safari
	                    	'menuItem:share:email',		// 邮件
	                    	'menuItem:copyUrl',			// 复制链接
	                    	'menuItem:share:brand'		// 一些特殊公众号	                    	
	                    	]
	                });
	                
	                wx.hideAllNonBaseMenuItem();
	                wx.hideOptionMenu();
	               // wx.onMenuShareAppMessage(wxShareData);
	               // wx.onMenuShareTimeline(wxShareData);
	               //  wx.onMenuShareQQ(wxShareData);
	               // wx.onMenuShareWeibo(wxShareData);
	            });
	            wx.error(function (res) {
	                // alert(res.errMsg);
	            });
        	}
        	else
        	{
        		 // alert("签名失败：" + data.message);
        	}
        		

        }
    });
//});
}



