# zengarden

This is a riff on Joel Holdbrooks' excellent [garden](https://github.com/noprompt/garden) library. This is also a library for rendering CSS in Clojure and Clojurescript. 


## Usage

Simply `(require '[zengarden.core :as zc])`, and start using the `(zc/css)` function. 

You can have nested syntax, and multiple definitions in the root list. Here's a few example's.

```
[[:html]]

[[:html {:height "100%" :display "flex"}]]

[[:html {:height "100%" :display "flex"}
  [:body {:display "flex"}]]]

[[:html {:height "100%" :display "flex"}
  [:body {:display "flex"}
   [:.myclass {:float "left"}]]
  [:footer {:background-color "black"}]]]

[[:html {:height "100%" :display "flex"}
  [:body {:display "flex"}
   [:.herclass :.hisclass {:float "left"}
    [:.thisclass {:color "white"}
     [:#anid {:color "blue"}]]]]]]
```


## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
