/*!
 * Custom Javascript
 */

/*font size increase & decrease*/
var min = -6
var medium = 0;
var max = 6;
var zoom = medium;
var zoomTags = ['p', 'label', 'h3', 'h4', 'h5', 'small', 'marquee', 'span', 'li'];

jQuery(document).ready(function() {
    $("#text-increase").click(function(){
        console.log('zoom-in');
        if(zoom < max){
            //zoom = zoom + 0.1;
            //$(".mainContent").css("font-size", zoom+"em");
            for (var i = 0; i < zoomTags.length; i++){
                zoomFont($(zoomTags[i]), 1);
            }
            zoom++;
        }
    });

    $("#text-decrease").click(function(){
        console.log('zoom-out');
        if(zoom > min){
            //zoom = zoom - 0.1;
            //$(".mainContent").css("font-size", zoom+"em");
            for (var i = 0; i < zoomTags.length; i++){
                zoomFont($(zoomTags[i]), -1);
            }
            zoom--;
        }
    });

});

function zoomFont(elements, count){
    if (elements != undefined && elements.length > 0){
        for (var i = 0; i < elements.length; i++){
            var element = elements[i];
            var currentSize = $(element).css('font-size');
            if (currentSize != undefined && currentSize != null){
                var afterSize = changeFontSize(currentSize, count);
                $(element).css('font-size', afterSize);
            }
        }
    }
}

function changeFontSize(currentFontSize, count){
    var unit = '';
    var number;
    var result = currentFontSize;
    if (currentFontSize.indexOf('px') > 0){
        unit = 'px';
    } else if (currentFontSize.indexOf('em') > 0){
        unit = 'em';
    } else if (currentFontSize.indexOf('pt') > 0){
        unit = 'pt';
    } else if (currentFontSize.indexOf('%') > 0){
        unit = '%';
    }
    if (unit != ''){
        number = currentFontSize.split(unit)[0];
        if (isNumeric(number)){
            if ('px' == unit || 'pt' == unit || '%' == unit){
                result = Number(number) + count;
            } else if ('em' == unit){
                result = Number(number) + count/10;
            }
            result += unit;
        }
    }
    return result;
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

/* Session timeout implementation */
/* Start **************************/
// How frequently to check for session expiration in miliseconds
var sess_pollInterval = 60000;
// How many minutes the session is valid for
var sess_expirationMinutes = 20;
// How many minutes before warning prompt
var sess_warningMinutes = 18;
var sess_logoutUrl = "";
var sess_intervalId;
var sess_lastActivity;
var sess_showWarningMess = false;

function initSessionMonitor(pollInterval, expirationMinutes, warningMinutes, logoutUrl){
    sess_pollInterval = pollInterval;
    sess_expirationMinutes = expirationMinutes;
    sess_warningMinutes = warningMinutes;
    if (sess_warningMinutes >= sess_expirationMinutes){
        sess_warningMinutes = sess_expirationMinutes - 1;
    }

    sess_logoutUrl = logoutUrl;


    if (sess_expirationMinutes > 0){
        sess_lastActivity = new Date();
        sessSetInterval();
        $(document).bind('click', function (){
            resetSessLastActivity();
        });
        $(document).scroll(function() {
            resetSessLastActivity();
        });
    }
}

function sessCheckTimeout(){
    var now = new Date();
    var diff = now - sess_lastActivity;
    var diffMinutes = diff / 1000 / 60;

    console.log('Your have been Inactive for ' + diffMinutes + ' minutes');
    if (diffMinutes > sess_expirationMinutes){
        sessDoLogout();
    } else if (diffMinutes >= sess_warningMinutes){
		 var mess = "It seems that you have been inactive for a while.<br/>" +
            "For security purposes, you will be automatically logged out if there is no activity within the next "
            + (sess_expirationMinutes - sess_warningMinutes) + " minutes.<br/>Please click to continue your session."
        if (!sess_showWarningMess){
            showWarning(mess);
            sess_showWarningMess = true;
        }
    }
}

function sessDoLogout(){
    sessClearInterval();
    window.location.href = sess_logoutUrl;
}

function sessSetInterval(){
    sess_intervalId = setInterval('sessCheckTimeout()', sess_pollInterval);
}

function sessClearInterval(){
    if(sess_intervalId != undefined || sess_intervalId != null){
        clearInterval(sess_intervalId);
    }
}

function resetSessLastActivity(){
    sess_lastActivity = new Date();
    if (sess_showWarningMess){
        $("#commonModal").modal("hide");
        sess_showWarningMess = false;
    }
}

/* End   **************************/
/* Session timeout implementation */

function showWarning(message){
    message = '<div class="alert alert-warning" role="alert">'+message+'</div>';
    $("#commonModalContent").html(message);
    $("#commonModal").modal("show");
    $("#commonModal").on('hidden.bs.modal', function(){
        $('body#top').attr('class', '');
    });
}





















