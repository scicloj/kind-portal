# kind-portal

<img src="https://raw.githubusercontent.com/scicloj/graphic-design/live/icons/Kindly.svg" alt="Kindly" align="right" width="128"/>
<img src="https://raw.githubusercontent.com/djblue/portal/master/resources/splash.svg" alt="Portal" align="right" width="128"/>

This library adapts the [Portal](https://github.com/djblue/portal/) visualization tool to support the [Kindly](https://scicloj.github.io/kindly/) convention for Clojure literate programming.

For example, this adapter allows people to view a single visualization created inside a notebook that would otherwise be rendered with [Clay](https://github.com/scicloj/clay).
Kindly establishes a common ground for Clojure learning resources that would work across different tools.
Portal provides an interactive way to view data inside your IDE or in a separate window.

## Usage

Add `org.scicloj/kind-portal` and `djblue/portal` as dev dependencies.

[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/kind-portal.svg)](https://clojars.org/org.scicloj/kind-portal)

[![Clojars Project](https://img.shields.io/clojars/v/djblue/portal.svg)](https://clojars.org/djblue/portal)

If you prefer, you can add them as a user dev dependencies instead,
so that they are available in all projects.
See `$HOME/.clojure/deps.edn` as described in [deps_sources](https://clojure.org/reference/deps_and_cli#deps_sources).

### Interaction

From the REPL you can require kind-portal, and then send forms to be visualized:

```clojure
(scicloj.kind-portal.v1.api/kindly-submit-context {:form '(+ 1 2)})
```

However, visualizations are most useful when you can invoke them with a key-binding.

It is recommended to create a REPL command to invoke `kindly-submit-context`:

```clojure
(do (require '[scicloj.kind-portal.v1.api :as kp])
    (kp/kindly-submit-context {:form (quote ~form-before-caret)}))
```

#### Emacs CIDER

Emacs users can make use of the [kind-portal.el](https://github.com/scicloj/kind-portal.el) package.

### Preparing values

Another use of kind-portal is functional, through the `prepare` function, which prepares a Kindly context as a Portal-ready values.

This may be used by other tools (e.g., Clay) that may wish to display Portal viewers.

For example:
```clojure
(require '[scicloj.kind-portal.v1.api :as kind-portal]
         '[scicloj.kindly.v4.kind :as kind])

(def portal-ready-value
  (kind-portal/prepare
   {:value (kind/hiccup [:div [:p "hello"]])}))

portal-ready-value
#_=> [:div [:p "hello"]]

(meta portal-ready-value)
#_=> {:kindly/kind :kind/hiccup,
      :portal.viewer/default :portal.viewer/hiccup}
```

## Examples

### Various kinds

See the [examples namespace](./examples/examples.clj) to try it out.

Portal looks like this:
![Portal example](https://user-images.githubusercontent.com/1986211/196015567-74ba9153-341a-4fd7-be47-2c26f0c88c2e.png)

### Standalone HTML

The [standalone-html namespace](./examples/standalone_html.clj) demonstrates how one may generate a standalone HTML file containing Portal views annotated by Kindly and prepared using kind-portal. See the resulting HTML page [here](https://scicloj.github.io/kind-portal/demo).

This is a self-contained demonstration of the method internally used by [Clay](https://scicloj.github.io/clay/) to [embed Portal views](https://scicloj.github.io/clay/#embedded-portal) in notebooks.

To generate the page, call the `generate!` function.

