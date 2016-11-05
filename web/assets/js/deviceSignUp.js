/**
 * Created by huangzhengyue on 2016/11/5.
 */
/**
 * Created by huangzhengyue on 8/31/16.
 */
"use strict";
var current_state = 0;
var sendMessageEnable=true;
$(document).ready(function () {
    //发送验证码按钮
    $("#send_message_button").click(function () {
        if(sendMessageEnable){
            $("#send_message_button").addClass('disable');
            $.ajax({
                type: "POST",
                data: {type: "sendMessage", phoneNumber: $("#phone").val()},
                url: "/servlet/shortMessageVerifying",
                success: function (feedBack) {
                    if(feedBack.success){
                        startCountDown(60,
                            function () {
                                $("#send_message_button").addClass('disable');
                                sendMessageEnable = false;
                            },
                            function () {
                                $("#send_message_button").removeClass('disable');
                                $("#send_message_button").text("发送验证短信");
                                sendMessageEnable = true;
                            });
                    }
                    else {
                        alert(feedBack.error);
                        $("#send_message_button").removeClass('disable');
                    }
                },
                error: function () {
                    alert("网络连接不佳");
                    $("#send_message_button").removeClass('disable');
                }
            });
        }
    });
    //下一步按钮
    $("#next-button").click(function () {
        if (current_state === 0) {
            //验证验证码
            $.ajax({
                type: "POST",
                data: {type: "verifying", verifyingCode: $("#verifyingCode").val()},
                url: "/servlet/shortMessageVerifying",
                success: function (feedBack) {
                    if (feedBack.success) {
                        //切换页面
                        $("#message-verify-form").removeClass("am-active");
                        $("#new-device-form").addClass("am-active");
                        current_state = 1;
                    }
                    else {
                        alert(feedBack.error);
                    }
                },
                error: function () {
                    alert("网络链接故障");
                }
            });

        }
        else if (current_state === 1) {
            var newDeviceID =$("#newDeviceID").val();
            var password = $("#password").val();
            var repeat = $("#repeatPassword").val();
            if (password == "") {
                alert("密码不能为空");
                return;
            }
            if (repeat !== password) {
                alert("密码不匹配");
                return;
            }
            $.ajax({
                type: "POST",
                data: {type: "newDevice", password: password,newDeviceID: newDeviceID},
                url: "/servlet/shortMessageVerifying",
                success: function (feedBack) {
                    if (feedBack.success) {
                        //切换页面
                        $("#new-device-form").removeClass("am-active");
                        $("#success").addClass("am-active");
                        $("#next-button").fadeOut();
                        current_state = 2;
                    }
                    else {
                        alert(feedBack.error);
                    }
                },
                error: function () {
                    alert("网络链接故障");
                }
            });

        }
        else if (current_state === 2) {
            $("#next-button").click(function () {
                window.location.href = "/login.html?p=" + $("#message").val();
            });
        }
    });
});


var second;
var timer;
function startCountDown(totalSeconds, doBegin, doAfter) {
    doBegin();
    second = totalSeconds;
    countDown(doAfter);
}


function countDown(doAfter) {
    second--;
    $("#send_message_button").text(second + "s后重试");
    if (second > 0) {
        timer = setTimeout(function (d) {
            countDown(doAfter);
        }, 1000);//调用自身实现
    }
    else {
        clearTimeout(timer);
        doAfter();
    }
}