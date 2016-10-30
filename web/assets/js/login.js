/**
 * Created by huangzhengyue on 2016/10/17.
 */
"use strict";
$(document).ready(function () {
    var loginNamePlaceHolder = getQueryString("p");
    $("#loginName").val(loginNamePlaceHolder);
    $("#change").click(function () {
        $("#verifyImage").attr("src", "/servlet/authImage?key=" + Math.random());
    });
    $("#login-button").click(function () {
        var loginName = $("#loginName").val();
        var password = $("#password").val();
        var verifyingCode = $("#verifyingCode").val();
        if (loginName === "") {
            alert("用户名为空");
            //刷新验证码
            $("#verifyImage").attr("src", "/servlet/authImage?key=" + Math.random());
            return;
        }
        if (password === "") {
            alert("密码为空");
            //刷新验证码
            $("#verifyImage").attr("src", "/servlet/authImage?key=" + Math.random());
            return;
        }
        if (verifyingCode === "") {
            alert("验证码为空");
            //刷新验证码
            $("#verifyImage").attr("src", "/servlet/authImage?key=" + Math.random());
            return;
        }
        var sendData = {};
        sendData["loginName"] = loginName;
        sendData["password"] = password;
        sendData["verifyingCode"] = verifyingCode;
        $.ajax({
            type: "POST", url: "/servlet/login", data: sendData, success: function (feedback) {
                if (feedback["login"]) {
                    //跳转
                    window.location.href = "control.html?sessionID="+feedback["sessionID"];
                }
                else {
                    //刷新验证码
                    $("#verifyImage").attr("src", "/servlet/authImage?key=" + Math.random());
                    alert(feedback.error);
                }
            }
        });
    });
});