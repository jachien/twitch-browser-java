<html>
<head>
    <meta charset="UTF-8"/>
    <title>Manage Games - Twitch Browser</title>
    <script src="/static/js/jquery-1.9.1.js"></script>
    <script src="/static/js/jquery-ui-1.10.3.custom.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/static/css/smoothness/jquery-ui-1.10.3.custom.min.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/twitchbrowser.css"/>

    <!-- autocomplete js modified from http://jqueryui.com/autocomplete/#remote-jsonp -->
    <script>
        $( document ).ready(function() {
            renderGames();
        });

        $(function() {
            $( "#game" ).autocomplete({
                source: function( request, response ) {
                    $.ajax({
                        url: "https://api.twitch.tv/kraken/search/games",
                        dataType: "jsonp",
                        data: {
                            query: request.term,
                            client_id: "ib5vu55l2rc4elcwyrqikyza4hio0y",
                            api_version: "5"
                        },
                        crossDomain: true,
                        success: function( data ) {
                            var games = data.games;
                            games.sort(function(a, b) {
                                return b["popularity"] - a["popularity"];
                            });
                            response($.map(games, function( item ) {
                                return {
                                    label: item.name,
                                    value: item.name
                                }
                            }));
                        }
                    });
                },
                minLength: 1,
                delay: 200,
                select: function( event, ui ) {
                    addGame(ui.item.label);
                    renderGames();
                },
                open: function() {
                    $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
                },
                close: function() {
                    $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
                }
            });
        });

        function writeGamesCookie(games) {
            var new_encoded_games = encodeGames(games);
            document.cookie = "settings=\"" + new_encoded_games + "\"; max-age=2147483647; path=/";
        }

        function addGame(game) {
            var games = readGamesCookie();
            var found = false;
            for (var i=0; i < games.length; i++) {
                if (games[i] == game) {
                    found = true;
                }
            }
            if (!found) {
                games.push(game);
                writeGamesCookie(games);
            }
        }

        function removeGame(game) {
            var games = readGamesCookie();
            for (var i=0; i < games.length; i++) {
                if (games[i] == game) {
                    games.splice(i, 1);
                    break;
                }
            }
            writeGamesCookie(games);
        }

        function readGamesCookie() {
            var encoded_games = readEncodedGames();
            return decodeGames(encoded_games);
        }

        function readEncodedGames() {
            var nameEQ = "settings=\"";
            var ca = document.cookie.split(';');
            for(var i=0;i < ca.length;i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') c = c.substring(1,c.length);
                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length-1);
            }

            var hardcoded_games = ["Dota 2", "Hearthstone", "Overwatch"];
            writeGamesCookie(hardcoded_games);
            return encodeGames(hardcoded_games);
        }

        function decodeGames(cookieValue) {
            if (cookieValue.length == 0) {
                return [];
            }
            var games = cookieValue.split(':');
            return $.map(games, function(game) {
                return decodeURIComponent(game.replace(/\+/g, ' '));
            });
        }

        function encodeGames(games) {
            games.sort(function(a, b) {
                return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
            });
            var encoded_game_array = $.map(games, function(game) {
                return urlEncode(game);
            });
            return encoded_game_array.join(":");
        }

        function urlEncode(str) {
            return encodeURIComponent(str).replace(/%20/g, '+');
        }

        function renderGames() {
            var encoded_games = readEncodedGames();
            var games = decodeGames(encoded_games);

            $("#games").empty()
            if (games.length > 0) {
                for (var i=0; i < games.length; i++) {
                    $("#games").append('<div class="game_item">' +
                            '<div class="game_item_boxart">' +
                            '<div class="game_item_remove"><a href="#" onclick="removeGame(\'' + games[i] + '\'); renderGames();">X Remove</a></div>' +
                            '<div><img src="http://static-cdn.jtvnw.net/ttv-boxart/' + urlEncode(games[i]) + '-136x190.jpg"></div>' +
                            '</div>' +
                            '<div class="game_item_name"><strong>' + games[i] + '</strong></div>' +
                            '</div>');
                }
            } else {
                $("#games").append("<p>You don't have any games saved. Add some using the input box at the top!</p>");
            }
        }
    </script>
</head>
<body>
<div style="margin-bottom: 10px;"><a href="/">Browse Live Streams</a></div>
<div>
    <label for="game">Add Game: </label>
    <input id="game">
</div>
<div id="games_container">
    <h2>My Games</h2>
    <div id="games"></div>
</div>
</body>
</html>