https://hub.docker.com/r/urbanslug/shadow-cljs

> npm run dev

Connect Proto-REPL to running nREPL server
(require '[shadow.cljs.devtools.server :as server])
(require '[shadow.cljs.devtools.api :as shadow])
(server/start!)

;; switch REPL to CLJS simply via
(shadow/browser-repl)
;; or
(shadow/node-repl)
;; or specific to a build configured in shadow-cljs.edn
(shadow/watch :the-build)
(shadow/repl :the-build)