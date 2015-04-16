# pho

phở is a Clojure Luminus webapp that will dynamically thumbnail and serve your photo directory.

Make two symlinks:

  resources/public/photos -> your photo directory
  resources/public/thumbs -> /var/tmp

I use /var/tmp so that the thumbnails get thrown away after 30 days by tmpwatch.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 Michael Bacarella

GPL

