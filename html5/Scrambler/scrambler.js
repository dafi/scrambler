var imageScrambler = {};

(function() {
    var lastUrl;

    function shuffle(arr) {
        for (var i = arr.length - 1; i >= 0; --i) {
            // swap indexes
            var r = parseInt(Math.random() * i);
            var t = arr[i];
            arr[i] = arr[r];
            arr[r] = t;
        }
        return arr;
    }
    
    function createRange(length) {
        var arr = new Array(length);
        for (var i = 0; i < length; i++) {
            arr[i] = i;
        }
        return arr;
    }

    function scrambleImage(image, piecesPerLine) {
        var w = image.width;
        var h = image.height;
        
        var canvas = document.getElementById('srcCanvas');
        canvas.setAttribute('width', w);
        canvas.setAttribute('height', h);
    
        var srcCtx = canvas.getContext('2d'); 
        srcCtx.drawImage(image, 0, 0);
        
        var dest = document.getElementById('destCanvas');
        dest.setAttribute('width', w);
        dest.setAttribute('height', h);
    
        var destCtx = dest.getContext('2d');
    
        var px = Math.ceil(w / piecesPerLine);
        var ph = Math.ceil(h / piecesPerLine);
        var arr = shuffle(createRange(piecesPerLine * piecesPerLine));
    
        //arr = [0, 1,2,3];
        console.log(arr);
        //var imageData = srcCtx.getImageData(100, 130, px, ph);
        //destCtx.putImageData(imageData, 0, 0);
        console.log('px %o, ph %o', px, ph);
        for (i = 0; i < piecesPerLine; i++) {
            for (j = 0; j < piecesPerLine; j++) {
                var n = arr[i * piecesPerLine +  j];
                var row = Math.floor(n / piecesPerLine);
                var col = n % piecesPerLine;
        
                console.log('n %o, row %o, col %o', n, row, col);
                var imageData = srcCtx.getImageData(row * px, col * ph, px, ph);
                destCtx.putImageData(imageData, j * px, i * ph);
            }
        }
    }

    this.scrambleImageFromUrl = function(url, piecesPerLine) {
        if (url == '') {
            return false;
        }
        piecesPerLine = parseInt(piecesPerLine);
        if (piecesPerLine < 2) {
            piecesPerLine = 2;
        }
        if (url == lastUrl) {
            scrambleImage($('#image')[0], piecesPerLine);
        } else {
            lastUrl = url;
            $('#image').load(function() {
                $('#dimensions').text('Image size: ' + this.width + 'x' + this.height);
                scrambleImage(this, piecesPerLine);
            });
            $('#image').attr('src', 'proxyImage.php?imageUrl=' + encodeURI(url));
        }
        return true;
    }
}).apply(imageScrambler);


