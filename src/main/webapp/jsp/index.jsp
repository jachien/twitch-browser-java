<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="model" scope="request" type="org.jchien.twitchbrowser.model.HomeModel"/>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Twitch Browser</title>
    <link rel="stylesheet" type="text/css" href="/static/css/twitchbrowser.css"/>
</head>
<body>
    <h1>Live Twitch.tv Streams</h1>

    <c:choose>
    <c:when test="${!empty model.streamList}">
    <div><a href="/settings">Manage Games</a></div>
    <div>
        <c:forEach var="stream" items="${model.streamList}">
        <div class="stream_item">
            <div><a href="${fn:escapeXml(stream.channelUrl)}"><img src="${fn:escapeXml(stream.previewUrl)}" /></a></div>
            <div>${fn:escapeXml(stream.status)}</div>
            <div><strong>${fn:escapeXml(stream.displayName)}</strong> playing <strong>${fn:escapeXml(stream.gameName)}</strong></div>
            <div>${fn:escapeXml(stream.numViewers)} viewers</div>
        </div>
        </c:forEach>
    </div>
    <div>Took ${model.formattedTimingString} seconds to get streams for ${model.numGames} games.</div>
    </c:when>
    <c:otherwise>
    <div style="margin-top: 10px;">No streams found. <a href="/settings">Add some games</a> to get started!</div>
    </c:otherwise>
    </c:choose>
</body>
</html>