/**
 * Created by huangzhengyue on 2016/10/31.
 */
var connectionDeviceID = getQueryString("connectionDeviceID");
var sessionID = getQueryString("sessionID");

$(document).ready(function () {
    "use strict";

    var editor_jq = $("#editor");
    var savedCode = getCookie("code");
    if (savedCode == "" || savedCode == undefined) {
        editor_jq.text('# 使用 python3.5 编辑代码\ndef run():\n    pass');
    } else {
        editor_jq.text(savedCode);
    }
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/chrome");
    editor.getSession().setMode("ace/mode/python");
    editor.gotoLine(4);


    $("#uploadCode").click(function () {

    });

    $("#saveCode").click(function () {
        setCookie("code", editor.getValue(), 365);
        alert("保存成功");
    });

    // $("#clearCode").click(function () {
    //     editor.destroy();
    // });

    $("#watchCompetition").click(function () {
        playVideo();
    });

    $("#up").click(function () {
        sendMovementCommand(connectionDeviceID, 0, 0.9);
    });

    $("#down").click(function () {
        sendMovementCommand(connectionDeviceID, 0, -0.9);
    });

    $("#left").click(function () {
        sendMovementCommand(connectionDeviceID, -0.9, 0);
    });

    $("#right").click(function () {
        sendMovementCommand(connectionDeviceID, 0.9, 0);
    });

    $("#detectON").click(function () {
        $.ajax({
            type: "POST",
            url: "/servlet/deviceControl",
            data: {
                type: "configure",
                commandJson: "{\"action\":\"startDetection\"}",
                requestedDeviceID: connectionDeviceID
            },
            success: function (feedback) {
                if (feedback.error) {
                    alert(feedback.error);
                    return;
                }
                else if (feedback.success) {
                    alert("设置成功");
                }
                else {
                    alert("bug-detected, please checkout the source code");
                }
            },
            error: function () {
                alert("网络连接不佳")
            }
        });
    });

    $("#detectOFF").click(function () {
        $.ajax({
            type: "POST",
            url: "/servlet/deviceControl",
            data: {
                type: "configure",
                commandJson: "{\"action\":\"stopDetection\"}",
                requestedDeviceID: connectionDeviceID
            },
            success: function (feedback) {
                if (feedback.error) {
                    alert(feedback.error);
                    return;
                }
                else if (feedback.success) {
                    alert("设置成功");
                }
                else {
                    alert("bug-detected, please checkout the source code");
                }
            },
            error: function () {
                alert("网络连接不佳")
            }
        });
    });


});


function playVideo() {
    //播放实时视频
    var video = $("#video");
    video.attr("src", "http://" + window.location.hostname + ":8999/?mj=" + sessionID + "," + connectionDeviceID);
    $("#videoPanel").fadeIn();
    $("#editorPanel").fadeOut();
}

function sendMovementCommand(deviceID, offsetX, offsetY) {
    $.ajax({
        url: "/servlet/deviceControl",
        type: "POST",
        data: {type: "movement", offsetX: offsetX, offsetY: offsetY, requestedDeviceID: deviceID},
        success: function (feedback) {
            if (feedback.success) {

            }
            if (feedback.error) {
                alert(feedback.error);
            }
        },
        error: function () {
            alert("请检查网络连接情况")
        }
    });
}
