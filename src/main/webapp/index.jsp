<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Twitch Browser</title>
    <style>
        body {
            font: 12px Helvetica,Arial,sans-serif;
            background-color: #dddddd;
        }
        .stream_item {
            width: 320px;
            display: inline-block;
            margin: 10px;
            vertical-align: top;
        }
        .stream_item div {
            margin: 2px;
        }
    </style>
</head>
<body>
<h1>Live Twitch.tv Streams</h1>
<c:forEach var="stream" items="${model.streamList}">
<div class="stream_item">
    <div><a href="${fn:escapeXml(stream.channelUrl)}"><img src="${fn:escapeXml(stream.previewUrl)}" /></a></div>
    <div>${fn:escapeXml(stream.status)}</div>
    <div><strong>${fn:escapeXml(stream.displayName)}</strong> playing <strong>${fn:escapeXml(stream.gameName)}</strong></div>
    <div>${fn:escapeXml(stream.numViewers)} viewers</div>
</div>
</c:forEach>
</body>
</html>