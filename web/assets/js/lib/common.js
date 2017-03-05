/**
 * Created by huangzhengyue on 8/27/16.
 */
var isIE = function (ver) {
    var b = document.createElement('b')
    b.innerHTML = '<!--[if IE ' + ver + ']><i></i><![endif]-->'
    return b.getElementsByTagName('i').length === 1
};
for (var i = 6; i <= 8; i++) {
    if (isIE(i)) {
        alert("TAT~对不起，您使用的浏览器版本过低，网页害羞☺️,萌萌哒郑光璨推荐您尝试一下新的浏览器哟~");
        location.href = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=monline_3_dg&wd=chrome&oq=google%20chrome&rsv_pq=9284ec510000c59c&rsv_t=005bBneWgXM3vxA1OUCDTrTvn36amqPDGuSf1ro0H77tmW56GbQs7ItFpR6IqxzwxWmv&rqlang=cn&rsv_enter=1&rsv_sug3=2&rsv_sug1=1&rsv_sug7=100&rsv_sug2=0&inputT=57&rsv_sug4=494&rsv_sug=2";
    }
}
"use strict";
function formToJson(formObj) {
    var o = {};
    var a = formObj.serializeArray();
    $.each(a, function () {

        if (this.value) {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || null);
            } else {
                if ($("[name='" + this.name + "']:checkbox", formObj).length) {
                    o[this.name] = [this.value];
                } else {
                    o[this.name] = this.value || null;
                }
            }
        }
    });
    return o;
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = decodeURI(window.location.search.substr(1)).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
//转码
function htmlspecialchars(str) {
    var s = "";
    if (str === undefined || str === null) {
        return "空";
    }
    if (str.length == 0) return "";
    for (var i = 0; i < str.length; i++) {
        switch (str.substr(i, 1)) {
            case "<":
                s += "&lt;";
                break;
            case ">":
                s += "&gt;";
                break;
            case "&":
                s += "&amp;";
                break;
            case " ":
                if (str.substr(i + 1, 1) == " ") {
                    s += " &nbsp;";
                    i++;
                } else s += " ";
                break;
            case "\"":
                s += "&quot;";
                break;
            case "\n":
                s += "<br>";
                break;
            default:
                s += str.substr(i, 1);
                break;
        }
    }
    return s;
}


function getCookie(c_name) {
    if (document.cookie.length > 0) {
        var c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
            c_start = c_start + c_name.length + 1;
            var c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) c_end = document.cookie.length;
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return ""
}

function setCookie(c_name, value, expiredays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + expiredays);
    document.cookie = c_name + "=" + escape(value) +
        ((expiredays == null) ? "" : "; expires=" + exdate.toGMTString())
}
