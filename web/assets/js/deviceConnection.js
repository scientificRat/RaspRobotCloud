/**
 * Created by huangzhengyue on 2016/10/31.
 */
var connectionDeviceID = getQueryString("connectionDeviceID");
var sessionID = getQueryString("sessionID");
$(document).ready(function () {
    "use strict";
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

    var videoPanel = $("#video");
    videoPanel.attr("src", "http://" + window.location.hostname + ":8999/?mj=" + sessionID + "," + connectionDeviceID);
    videoPanel.fadeIn();

});

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
