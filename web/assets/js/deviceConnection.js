/**
 * Created by huangzhengyue on 2016/10/31.
 */
var connectionDeviceID = getQueryString("connectionDeviceID");
var sessionID = getQueryString("sessionID");
$(document).ready(function() {
    "use strict";
    $("#up").click(function() {

    });

    $("#down").click(function() {

    });

    $("#left").click(function() {

    });

    $("#right").click(function() {

    });

    var videoPanel =$("#video");
    videoPanel.attr("src","http://"+window.location.hostname + ":8999/?mj=" +sessionID+","+connectionDeviceID);
    videoPanel.fadeIn();

});
