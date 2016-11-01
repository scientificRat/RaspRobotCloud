/**
 * Created by huangzhengyue on 2016/10/31.
 */
"use strict";
var sessionID;
var selectedDeviceID = null;
$(document).ready(function () {
    //获取登录信息
    $.ajax({
        type: "POST",
        url: "/servlet/loginState",
        success: function (feedBack) {
            if(feedBack["login"]){
                sessionID =feedBack["sessionID"];
                //显示登录账号
                $("#userName").text(feedBack["userName"]);
            }
            else {
                alert("session time out");
                window.location = "index.html";
            }
        },
        error: function () {
            alert("session time out");
            window.location = "index.html";
        }
    });

    // 获取设备列表
    updateDeviceList();
    $("#refresh").click(function () {
        updateDeviceList();
    });

    //连接设备按钮
    $("#connectDevice").click(function () {
        if(selectedDeviceID!=null){
            window.location= "deviceConnection.html?sessionID="+sessionID+"&connectionDeviceID"+ selectedDeviceID;
        }
        else {
            alert("请先选择设备")
        }
    });
    //退出登录按钮
    $("#logout").click(function () {
        $.ajax({
            type: "POST", url: "/servlet/login", data: {logout:"logout"}, success: function (feedback) {
                if (feedback["success"]) {
                    //跳转登录界面
                    window.location.href = "index.html";
                }
                else {
                    alert("session Timeout, please contact the administrator");
                }
            }
        });
    });
});

function updateDeviceList() {
    $("#cars").html("");
    $("#refresh i").addClass("am-animation-spin");
    $(".dashboard").fadeOut();
    $.ajax({
        url:"/servlet/deviceInfo",
        type: "POST",
        data:{type:"queryAll"},
        success:function (feedback) {
            if(feedback["error"]!==undefined){
                alert(feedback.error);
                window.location="index.html";
                return;
            }
            var content="";
            for(var i in feedback){
                var device=feedback[i];
                var isOnline = device["online"];
                var deviceID= device["deviceID"];
                if(isOnline){
                    content +="<div class='car_item online' onclick='carsOnclickListener(this)' dt='"+deviceID+"'><span>" + deviceID;
                    content +="(在线)</span> </div>";
                }
                else {
                    content +="<div class='car_item online' onclick='carsOnclickListener(this)' dt='"+deviceID+"'><span>" + deviceID;
                    content +="(离线)</span> </div>";
                }
            }
            $("#cars").html(content);
            $("#refresh i").removeClass("am-animation-spin");


        },
        error:function () {
            alert("请检查网络");
            return;
        }
    });
}

function carsOnclickListener(that) {
    $("#cars").find("car_item").removeClass("connect");
    selectedDeviceID = $(that).attr("dt");
    $("#deviceName").text("设备名:"+selectedDeviceID);
    $(that).addClass("connect");
    $(".dashboard").fadeIn();
}
