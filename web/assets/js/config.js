var url = "http://192.168.1.128:8080/?action=imageCommand";

$(document).ready(function() {
    "use strict";
    //brightness
    $("#brightness").change(function () {
        var brightness = parseInt($("#brightness").val());
        $("#brightnessValue").val(brightness);
    });
    $("#brightnessValue").change(function () {
        var brightnessValue = parseInt($("#brightnessValue").val());
        if(brightnessValue > 100)
            brightnessValue = 100;
        if (brightnessValue < 0)
            brightnessValue = 0;
        $("#brightness").val(brightnessValue);
    });
    $("#brightnessValue").keypress(function (e) {
        var keynum;

        if (window.event) // IE
        {
            keynum = e.keyCode;
        } else if (e.which) // Netscape/Firefox/Opera
        {
            keynum = e.which;
        }

        if(keynum == 13)
        {
            var brightnessValue = parseInt($("#brightnessValue").val());
            if(brightnessValue > 100)
                brightnessValue = 100;
            if (brightnessValue < 0)
                brightnessValue = 0;
            $("#brightnessValue").val(brightnessValue);
            $("#brightness").val(brightnessValue);
        }
    });


    //contrast
    $("#contrast").change(function () {
        var contrast = parseInt($("#contrast").val());
        $("#contrastValue").val(contrast);
    });

    $("#contrastValue").change(function () {
        var contrastValue = parseInt($("#contrastValue").val());
        if(contrastValue > 100)
            contrastValue = 100;
        if (contrastValue < 0)
            contrastValue = 0;
        $("#contrast").val(contrastValue);
    });
    $("#contrastValue").keypress(function (e) {
        var keynum;

        if (window.event) // IE
        {
            keynum = e.keyCode;
        } else if (e.which) // Netscape/Firefox/Opera
        {
            keynum = e.which;
        }

        if(keynum == 13)
        {
            var contrastValue = parseInt($("#contrastValue").val());
            if(contrastValue > 100)
                contrastValue = 100;
            if (contrastValue < 0)
                contrastValue = 0;
            $("#contrastValue").val(contrastValue);
            $("#contrast").val(contrastValue);
        }
    });

    //saturation
    $("#saturation").change(function () {
        var saturation = parseInt($("#saturation").val());
        $("#saturationValue").val(saturation);
    });

    $("#saturationValue").change(function () {
        var saturationValue = parseInt($("#saturationValue").val());
        if(saturationValue > 100)
            saturationValue = 100;
        if (saturationValue < 0)
            saturationValue = 0;
        $("#saturation").val(saturationValue);
    });
    $("#saturationValue").keypress(function (e) {
        var keynum;

        if (window.event) // IE
        {
            keynum = e.keyCode;
        } else if (e.which) // Netscape/Firefox/Opera
        {
            keynum = e.which;
        }

        if(keynum == 13)
        {
            var saturationValue = parseInt($("#saturationValue").val());
            if(saturationValue > 100)
                saturationValue = 100;
            if (saturationValue < 0)
                saturationValue = 0;
            $("#saturationValue").val(saturationValue);
            $("#saturation").val(saturationValue);
        }
    });

    
    //width
    $("#width").change(function () {
        var width = parseFloat($("#width").val());
        var height = parseFloat($("#height").val());
        var Fratio = 1.33333333333333; // Fratio is equal 4/3
        var Tratio = 0.75000000000000; // Tratio is equal 3/4

        //change width numver value
        $("#widthValue").val(width);

        if (Math.abs(width/height - Fratio) < 0.00000000000001){
        }
        else {
            var newHeight = Math.round(width * Tratio);
            $("#height").val(newHeight);
            $("#heightValue").val(newHeight);
        }
    });
    $("#widthValue").change(function () {
        var widthValue = parseFloat($("#widthValue").val());
        var heightValue = parseFloat($("#heightValue").val());
        var Fratio = 1.33333333333333; // Fratio is equal 4/3
        var Tratio = 0.75000000000000; // Tratio is equal 3/4

        if(widthValue > 1280)
            widthValue = 1280;
        if (widthValue < 0)
            widthValue = 0;

        //change width numver value
        $("#width").val(widthValue);

        if (Math.abs(widthValue/heightValue - Fratio) < 0.00000000000001){
        }
        else {
            var newHeight = Math.round(widthValue * Tratio);
            $("#height").val(newHeight);
            $("#heightValue").val(newHeight);
        }
    });
	$("#widthValue").keypress(function (e) {
        var keynum;

        if (window.event) // IE
        {
            keynum = e.keyCode;
        } else if (e.which) // Netscape/Firefox/Opera
        {
            keynum = e.which;
        }

        if(keynum == 13)
        {
            var widthValue = parseFloat($("#widthValue").val());
			var heightValue = parseFloat($("#heightValue").val());
			var Fratio = 1.33333333333333; // Fratio is equal 4/3
			var Tratio = 0.75000000000000; // Tratio is equal 3/4

			if(widthValue > 1280)
				widthValue = 1280;
			if (widthValue < 160)
				widthValue = 160;

			//change width numver value
			$("#width").val(widthValue);
			$("#widthValue").val(widthValue);

			if (Math.abs(widthValue/heightValue - Fratio) < 0.00000000000001){
			}
			else {
				var newHeight = Math.round(widthValue * Tratio);
				$("#height").val(newHeight);
				$("#heightValue").val(newHeight);
			}
        }
    });


    //height
    $("#height").change(function () {
        var width = parseFloat($("#width").val());
        var height = parseFloat($("#height").val());
        var Fratio = 1.33333333333333; // Fratio is equal 4/3
        var Tratio = 0.75000000000000; // Tratio is equal 3/4

        //change width numver value
        $("#heightValue").val(height);

        if (Math.abs(width/height - Fratio) < 0.00000000000001){
        }
        else {
            var newWidth = Math.round(height * Fratio);
            $("#width").val(newWidth);
            $("#widthValue").val(newWidth);
        }
    });
    $("#heightValue").change(function () {
        var widthValue = parseFloat($("#widthValue").val());
        var heightValue = parseFloat($("#heightValue").val());
        var Fratio = 1.33333333333333; // Fratio is equal 4/3
        var Tratio = 0.75000000000000; // Tratio is equal 3/4

        if(heightValue > 960)
            heightValue = 960;
        if (heightValue < 120)
            heightValue = 120;

        //change width numver value
        $("#height").val(heightValue);

        if (Math.abs(widthValue/heightValue - Fratio) < 0.00000000000001){
        }
        else {
            var newWidth = Math.round(heightValue * Fratio);
            $("#width").val(newWidth);
            $("#widthValue").val(newWidth);
        }
    });
	$("#heightValue").keypress(function (e) {
        var keynum;

        if (window.event) // IE
        {
            keynum = e.keyCode;
        } else if (e.which) // Netscape/Firefox/Opera
        {
            keynum = e.which;
        }

        if(keynum == 13)
        {
			var widthValue = parseFloat($("#widthValue").val());
			var heightValue = parseFloat($("#heightValue").val());
			var Fratio = 1.33333333333333; // Fratio is equal 4/3
			var Tratio = 0.75000000000000; // Tratio is equal 3/4

			if(heightValue > 960)
				heightValue = 960;
			if (heightValue < 120)
				heightValue = 120;

			//change width numver value
			$("#height").val(heightValue);
			$("#heightValue").val(heightValue);

			if (Math.abs(widthValue/heightValue - Fratio) < 0.00000000000001){
			}
			else {
				var newWidth = Math.round(heightValue * Fratio);
				$("#width").val(newWidth);
				$("#widthValue").val(newWidth);
			}
        }
    });
    


    $("#submitButton").click(function() {
        var ImageValue = new Object();
        ImageValue.BRIGHTNESS = parseInt($("#brightness").val()) ;
        ImageValue.CONTRAST = parseInt($("#contrast").val());
        ImageValue.SATURATION = parseInt($("#saturation").val());
        //ImageValue.WIDTH = parseInt($("#width").val());
        ImageValue.WIDTH = 320;
        //ImageValue.HEIGHT = parseInt($("#height").val());
        ImageValue.HEIGHT = 240;
        var ImageJson = JSON.stringify(ImageValue);
        //alert(ImageJson);

        $.get(url,ImageJson);
    });

});
