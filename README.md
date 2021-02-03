# MatchingEngine
Simple matching engine for a single asset.

Start the main class Server.java.

Use any telnet like application like netcat to connect to port 9887 on the machine the server is running on.
currently the following commands are supported
 b nnn @ xxx       # buy nnn units at price xxx.  (only integer values are supported)
 s nnn @ xxx       # sell nnn units at price xxx
 g ii              # get info for order ii
 o                 # get the current orderbook sizes
 
 more features to be added

