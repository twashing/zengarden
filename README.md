# zengarden

Zengarden is a riff on Joel Holdbrooks' excellent [garden](https://github.com/noprompt/garden) library. It is a Clojure library that uses an extension language for rendering CSS.

It's meant to cover most of CSS3 syntax. The spirit of the library is to have declarative syntax, and be controllable from edn. 

There's going to be a lot of missing stuff that I haven't thought of. I've just built it for my own use cases and then some. When that happens, you can i) pass in raw CSS, ii) send me feature requests or iii) send me a pull request. These are some things you get out of the box. 


## Usage

Simply `(require '[zengarden.core :as zc])`, and start using the `(zc/css)` function. You can have nested syntax, and multiple definitions in the root list. Here's a few example's, with the resulting output.


*Basic Nesting*
```
(require '[zengarden.core :as zc])

(zc/css [[:html {:height "100%" :display "flex"}
          [:body {:display "flex"}]]])
          
 =>
 
html{ 
  display : flex; 
  height : 100%; }

html body{ 
  display : flex; }
```

*Nesting, Grouping and Ancestors*
```
(zc/css [[:html {:height "100%" :display "flex"}
          [:body {:display "flex"}
           [:.herclass :.hisclass {:float "left"}
            [:.thisclass {:color "white"}
             [:#anid {:color "blue"}]]]]]])

=> 

html{ 
  display : flex; 
  height : 100%; }

html body{ 
  display : flex; }

html body .herclass{ 
  float : left; }
html body .hisclass{ 
  float : left; }

 html body .herclass .thisclass, html body .hisclass .thisclass{ 
  color : white; }

 html body .herclass .thisclass #anid, html body .hisclass .thisclass #anid{ 
  color : blue; }
```            

*Supported AtRules - @namespace, @import and @media*
```

(zc/css [[:at-namespace {:prefix "svg" :url "http://www.w3.org/2000/svg"}]
         [:at-import {:url "http://fonts.googleapis.com/css?family=Gentium+Book+Basic:700italic"
                      :media-queries ['(:min-width "700px") "handheld" "and" '(:orientation "landscape")]}]
         [:html {:height "100%" :display "flex"}
          [:body {:display "flex"}
           [:.herclass :.hisclass {:float "left"}
            [:.thisclass {:color "white"}
             [:#anid {:color "blue"}]]]]]
         [:at-media {:media-queries ["screen" "print" "and" '(:orientation "landscape")]}
          [:.sidebar {:display "none"}]]
         [:at-media {:media-queries ["screen" "print"]}
          [:.sidebar {:display "block"}]]])

=>
@namespace svg url(http://www.w3.org/2000/svg);


html{ 
  display : flex; 
  height : 100%; }

html body{ 
  display : flex; }

html body .herclass{ 
  float : left; }
html body .hisclass{ 
  float : left; }

 html body .herclass .thisclass, html body .hisclass .thisclass{ 
  color : white; }

 html body .herclass .thisclass #anid, html body .hisclass .thisclass #anid{ 
  color : blue; }
@media screen print and (orientation: landscape) {

.sidebar{ 
  display : none; }
}
@media screen print {

.sidebar{ 
  display : block; }
}
```

## License

Copyright © 2014 Interrupt Software Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

