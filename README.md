# pho

phở is a Clojure Luminus webapp that will dynamically thumbnail and serve your photo directory.

Make two directories/symlinks:

  - resources/public/photos -> your photo directory
  - resources/public/thumbs -> writable thumbs directory

Run 'lein ring server-headless' or deploy to Tomcat.  Fin.

## TODO

  - It's probably needlessly slow, serving images through the ring server
  - Make the tomcat deploy actually work.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 Michael Bacarella

GPL
