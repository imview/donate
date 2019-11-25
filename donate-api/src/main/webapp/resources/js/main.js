/**
 * 获取url参数
 * 
 * @param {string}
 *            name 参数名称
 */
function getUrlKey(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
}

// ucsajax
var ajaxingCount = 0;
(function ($) {
    $.ucsajax = function (opt) {
        
        // 默认自定义参数
        var defaults = {
            apiurl: "",
            apidata: "",
            // contentType:"application/json",
            isShowLoading: true,
            apisuccess: function(result) {},
            apierror: function(result) { $.ucsalert(result.message); },
            ajaxerror: function(request, error, exception) { $.ucsalert("网络错误，请刷新重试");}
        }
        var options = $.extend({}, defaults, opt);
      

        // 默认ajax参数
        var ajaxopt = {
            cache: false,
            url: options.apiurl,
            data:options.apidata,
            beforeSend: function (request) {
                if (options.isShowLoading) {
                    ajaxingCount++;
                    $('#loadinglayer').show();
                }
            },
            success: function (result) {
                console.log(result);
                if (result.isSuccess) {
                    options.apisuccess(result);
                } else {
                    // 您的账号在其他设备登录，请确认是否本人操作
                   /*
					 * if (result.code == "008") { //$.ucsalert('授权超时，重新授权中',
					 * 2000, null, function () { //
					 * window.location.replace("/WorkWeiXin/Index"); //}); }
					 * else {
					 */
                        options.apierror(result);
                    // }
                }
            },
            error: function (request, error, exception) {
                console.log(request);
                options.ajaxerror(request, error, exception);
            },
            complete: function () {
                if (options.isShowLoading) {
                    ajaxingCount--;
                    if (ajaxingCount <= 0) {
                        ajaxingCount = 0;
                        $('#loadinglayer').hide();
                    }
                }
            }
        }
        
        options = $.extend({}, ajaxopt, options);

        if (options.isShowLoading) {
            if ($('#loadinglayer').length <= 0) {
                $('body').append('<div id="loadinglayer" class="loading-layer-box"><div class="loading-layer"><div class="loading"></div></div></div>');
            }
            $('#loadinglayer').show();
        }

        $.ajax(options);

    }
})(jQuery);

// ucsalert
(function($) {
    $.ucsalert = function(msg) {
        // 删除已存在的弹出框
        if ($("#tipslayer").length > 0) {
            $("#tipslayer").remove();
        }
        var html = '<div id="tipslayer" class="tips-layer"><div class="tips-layer-box"><div class="tips-layer-title">提示</div><div class="tips-layer-content">{提示内容}</div><a href="javascript:;" class="tips-layer-btn">确定</a></div></div>'
        html = html.replace("{提示内容}", msg);
        $('body').append(html);
        $('#tipslayer .tips-layer-btn').on("click", function () {
            $(this).parents('.tips-layer').fadeOut();
        });
        $("#tipslayer").show();

    }
})(jQuery);



Storage.prototype.getObject = function (key) {
    var _self = this;    
    var data = _self.getItem("gdckpf_" + key);
    if (data == null || data=="")
        return null;
    else
        return JSON.parse(data);
}

Storage.prototype.setObject = function (key, data) {
    var _self = this;
    _self.setItem("gdckpf_" + key, JSON.stringify(data));
}

Storage.prototype.getUserData = function (key) {
    var _self = this;
    return _self.getObject(getUserInfo().openId + "_" + key)
}

Storage.prototype.setUserData = function (key, data) {
    var _self = this;
    _self.setObject(getUserInfo().openId + "_"  + key, data);
}

// 获取当前用户信息
function getUserInfo() {
    if (localStorage.getObject("user") == null)
        window.location = "/static/system/oauth.html?action=" + window.location;
    return localStorage.getObject("user");
}

function strToDate(str) {
    new Date(str.replace(/-/g, "/").replace('T', ''));
}

function formatDate(obj, fmt) {
    if (obj == null)
        return "";
    if (Object.prototype.toString.call(obj) == "[object Date]")
        date = obj;
    else if (Object.prototype.toString.call(obj) == "[object String]") {
        if (obj == "")
            return "";
        date = strToDate(obj);
    } else {
        return "";
    }

    var date = this.toDate();
    var o = {
        "M+": date.getMonth() + 1, // 月份
        "d+": date.getDate(), // 日
        "h+": date.getHours() % 12 == 0 ? 12 : date.getHours() % 12, // 小时
        "H+": date.getHours(), // 小时
        "m+": date.getMinutes(), // 分
        "s+": date.getSeconds(), // 秒
        "q+": Math.floor((date.getMonth() + 3) / 3), // 季度
        "S": date.getMilliseconds() // 毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}



// 千分位
function toThousands(num) {
	var pointNum = "";
	var numStr= num.toString();
	var numVal = num;
	if(numStr.indexOf('.')>=0)
	{
		pointNum = numStr.substring(numStr.lastIndexOf('.'));
		numVal = parseInt(numStr.substring(0, numStr.lastIndexOf('.')));
	}
	
	var numVal = (numVal || 0).toString(), result = '';
	while (numVal.length > 3) {
	    result = ',' + numVal.slice(-3) + result;
	    numVal = numVal.slice(0, numVal.length - 3);
	}
	if (numVal) 
	{ 
		result = numVal + result; 
	}
	return result + pointNum;
}



// 获取时间
function getDonateTime(type, items)
{
   var time =  0;
   if(type==1)   // 最新的时间点
   {
       for(var i=0; i<items.length; i++)
       {
            if(time==0)
            {
                time = items[i].createTime;
                continue;
            }
            
            if(time < items[i].createTime)
            {
                time = items[i].createTime;
            }
       }
   }
   else         // 最后的时间点
   {
       for(var i=0; i<items.length; i++)
       {
            if(time==0)
            {
                time = items[i].createTime;
                continue;
            }
            
            if(time > items[i].createTime)
            {
                time = items[i].createTime;
            }
       }
   }
   var retTime;
   for(var i=0; i<items.length; i++)
   {
        if(time==items[i].createTime)
        {
             retTime = items[i].createTimeStr;
             break;
        }
   }       
   return retTime;
}

function getQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}

Vue.filter('number', function(s,n) {
	n=n||2;
	 n = n > 0 && n <= 20 ? n : 2;
     s = parseFloat((s + '').replace(/[^\d\.-]/g, '')).toFixed(n) + '';
     var l = s.split('.') [0].split('').reverse(),
         r = s.split('.') [1];
     var  t = '';
     for (var i = 0; i < l.length; i++)
     {
         t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? ',' : '');
     }
     return t.split('').reverse().join('') + '.' + r;
});


Date.prototype.Format = function (fmt) { 
    var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "h+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds() // 毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}