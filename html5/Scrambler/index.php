<!DOCTYPE html>

<html>
<head>
    <title>Image Scrambler</title>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <link type="text/css" href="scrambler.css" rel="stylesheet"/>
    <script src="scrambler.js"></script>
    
    <script>
    $(function() {
        $('#url-button').click(function() {
            if (!imageScrambler.scrambleImageFromUrl($('#url').val(), $('#piecesPerLine').val())) {
                alert('Invalid URL');
            }
        });
    })
    </script>
</head>

<body>
    <h1>Scramble an image like a puzzle</h1>
    <form>
        <fieldset>
            <legend>Number of pieces per line</legend>
            <input id="piecesPerLine" type="number" value="5" size="4"/>
        </fieldset>
        <fieldset>
            <legend>Image URL to scramble</legend>
                <input id="url" type="text" size="100"/>
                <input id="url-button" type="button" value="Scramble"/>
        </fieldset>
    </form>
    
    <img id="image" src="" style="display:none"></img>

    <div id="info">
        <p id="dimensions"/>
    </div>
    
    <canvas id="srcCanvas"></canvas>
    <canvas id="destCanvas"></canvas>

<div style="clear: both"></div>
<div id="footer">
    <ul>
        <li><span>2012-<?php echo date("Y")?> Scrambler writter by Davide Ficano</span></li>
        <li><a href="http://github.com/dafi/scrambler">Github</a></li>
    </ul>
</div>

</body>
</html>
