(ns tesseract.dom
  (:refer-clojure :exclude [map meta time var])
  #+cljs
  (:require-macros
    [tesseract.dom :refer [defelement]])
  (:require
    [clojure.string]
    [tesseract.impl.vdom :as vdom]
    [tesseract.impl.patch :as impl.patch]
    [tesseract.attrs]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Patches

(defrecord SetAttributes [attrs]
  impl.patch/IPatch
  (-patch! [_ node]
    ;; TODO
    ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Public API

(defrecord Element [tag attrs children]
  tesseract.impl.vdom/IVirtualRenderNode
  (-diff [_ other]
    (let [other-attrs (:attrs other)
          diff-map (into {}
                         (for [k (reduce conj (keys attrs) (keys other-attrs))
                               :let [self-val (get attrs k)
                                     other-val (get other-attrs k)]
                               :when (not= self-val other-val)]
                           [(name k) other-val]))]
      (when-not (empty? diff-map)
        (->SetAttributes diff-map))))

  Object
  (toString [this]
    (let [tag-name (-> tag name str)
          attrs (or (tesseract.attrs/get-attrs this) attrs)]
      (str
        "<"
        tag-name
        (when (seq attrs)
          (->> attrs
               (filter #(satisfies? tesseract.attrs/IAttributeValue (val %)))
               (clojure.core/map tesseract.attrs/to-element-attribute)
               (clojure.string/join " ")
               (str " ")))
        ">"
        (when (seq children)
          (clojure.string/join (clojure.core/map str (flatten children))))
        "</" tag-name ">"))))

#+clj
(defmacro defelement
  [tag]
  (let [tag-kw (keyword tag)]
    `(let [base-element# (new Element ~tag-kw nil [])]
       (defn ~tag
         ([]
          base-element#)
         ([attrs#]
          (if (nil? attrs#)
            base-element#
            (new Element ~tag-kw attrs# [])))
         ([attrs# & children#]
          (new Element ~tag-kw attrs# (vec children#)))))))

; The commented out elements below are deprecated elements. There are others that are still
; available and considered not-best-practice (e.g. <b> and <i>), but they are not officially
; obsolete.
(defelement a)
(defelement abbr)
;(defelement acronym)
(defelement address)
;(defelement applet)
(defelement area)
(defelement article)
(defelement aside)
(defelement audio)
(defelement b)
(defelement base)
;(defelement basefont)
(defelement bdi)
(defelement bdo)
;(defelement bgsound)
;(defelement big)
;(defelement blink)
(defelement blockquote)
(defelement body)
(defelement br)
(defelement button)
(defelement canvas)
(defelement caption)
;(defelement center)
(defelement cite)
(defelement code)
(defelement col)
(defelement colgroup)
;(defelement content)
(defelement data)
(defelement datalist)
(defelement dd)
;(defelement decorator)
(defelement del)
(defelement details)
(defelement dfn)
;(defelement dir)
(defelement div)
(defelement dl)
(defelement dt)
(defelement element)
(defelement em)
(defelement embed)
(defelement fieldset)
(defelement figcaption)
(defelement figure)
;(defelement font)
(defelement footer)
(defelement form)
;(defelement frame)
;(defelement frameset)
(defelement h1)
(defelement h2)
(defelement h3)
(defelement h4)
(defelement h5)
(defelement h6)
(defelement head)
(defelement header)
;(defelement hgroup)
(defelement hr)
(defelement html)
(defelement i)
(defelement iframe)
(defelement img)
(defelement input)
(defelement ins)
;(defelement isindex)
(defelement kbd)
(defelement keygen)
(defelement label)
(defelement legend)
(defelement li)
(defelement link)
;(defelement listing)
(defelement main)
(defelement map)
(defelement mark)
;(defelement marquee)
(defelement menu)
(defelement menuitem)
(defelement meta)
(defelement meter)
(defelement nav)
;(defelement nobr)
;(defelement noframes)
(defelement noscript)
(defelement object)
(defelement ol)
(defelement optgroup)
(defelement option)
(defelement output)
(defelement p)
(defelement param)
;(defelement plaintext)
(defelement pre)
(defelement progress)
(defelement q)
(defelement rp)
(defelement rt)
(defelement ruby)
(defelement s)
(defelement samp)
(defelement script)
(defelement section)
(defelement select)
;(defelement shadow)
(defelement small)
(defelement source)
;(defelement spacer)
(defelement span)
;(defelement strike)
(defelement strong)
(defelement style)
(defelement sub)
(defelement summary)
(defelement sup)
(defelement table)
(defelement tbody)
(defelement td)
;(defelement template)
(defelement textarea)
(defelement tfoot)
(defelement th)
(defelement thead)
(defelement time)
(defelement title)
(defelement tr)
(defelement track)
;(defelement tt)
(defelement u)
(defelement ul)
(defelement var)
(defelement video)
(defelement wbr)
;(defelement xmp)
