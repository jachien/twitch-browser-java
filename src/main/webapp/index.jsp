<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>App Engine Demo</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
</head>
<body>
<div id="result">Loading...</div>

<script>
    $(document).ready(function() {
        $.getJSON('/index', function(data) {
            $('#result').html("Hello, " + data.name);
        });
    });
</script>
</body>
</html>