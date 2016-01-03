/**
    * Created by RJA.
    */
$(function () {
    var poss = [];
    var items = [];
    var images = document.getElementsByTagName('img');
    var aLinks =  document.getElementsByTagName('a');

    for (var i = 0; i < 10; i++) {
        poss[i] = i * 20 - 100 + '%';
    }
    for (var i = 0; i < 10; i++) {
        items[i] = document.getElementById("i" + i);
    }

    var moveCount = 0;
    var lastImageIndex = 9;
    var maxSrc = images[9].src;

    setInterval(function send() {
        moveCount++;
        for (var i = 0; i < 10; i++) {
            var pos = (i + moveCount) % 10;
            items[i].style.left = poss[pos];

            if (pos == 0 && i == lastImageIndex) {
                sendRequest();
            }
        }
    }, 3000);

    function sendRequest() {
        var data = {"lastImageSrc" : maxSrc};
        $.get("/getImage", data, put, "json");
    }

    function put(returnedData) {
        var putSrc = returnedData.imagePath;
        var putMessage = returnedData.putMessage;
        var putLink = returnedData.putLink;

        if (putSrc != "nothing") {
            images[lastImageIndex].src = putSrc;
            aLinks[lastImageIndex].title = putMessage;
            aLinks[lastImageIndex].href = putLink;

            maxSrc = images[lastImageIndex].src;
            lastImageIndex = (lastImageIndex + 9) % 10;
        }
    }
});


$(function () { document.getElementById("addform").style.visibility = "hidden" })

function setVisibility() {
    var elem = document.getElementById("addform");
    if (elem.style.visibility == "hidden") {
        elem.style.visibility = "visible";
    }
    else {
        elem.style.visibility = "hidden";
    }
}