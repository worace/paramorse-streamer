(ns paramorse-streamer.core
  (require [org.httpkit.timer :as timer]
           [org.httpkit.client :as client]
           [org.httpkit.server :as http]
           [clojure.string :refer [split join upper-case]]))

(def texts ["The history of all hitherto existing society is the history of class struggles."
            "Freeman and slave, patrician and plebeian, lord and serf, guildmaster and journeyman, in a word, oppressor and oppressed, stood in constant opposition to one another, carried on an uninterrupted, now hidden, now open fight, that each time ended, either in the revolutionary reconstitution of society at large, or in the common ruin of the contending classes."
            "A specter is haunting Europe—the specter of Communism. All the powers of old Europe have entered into a holy alliance to exorcise this specter; Pope and Czar, Metternich and Guizot, French radicals and German police spies."
            "Where is the party in opposition that has not been decried as Communistic by its opponents in power? Where the opposition that has not hurled back the branding reproach of Communism, against the more advanced opposition parties, as well as against its reactionary adversaries?"
            "The bourgeoisie, wherever it has got the upper hand, has put an end to all feudal, patriarchal, idyllic relations. It has pitilessly torn asunder the motley feudal ties that bound man to his 'natural superiors,' and has left remaining no other nexus between man and man than naked self-interest, callous 'cash payment.' It has drowned the most heavenly ecstasies of religious fervor, of chivalrous enthusiasm, of philistine sentimentalism, in the icy water of egotistical calculation. It has resolved personal worth into exchange value, and in place of the numberless indefeasible chartered freedoms, has set up that single, unconscionable freedom—Free Trade. In one word, for exploitation, veiled by religious and political illusions, it has substituted naked, shameless, direct, brutal exploitation."
            "The bourgeoisie has stripped of its halo every occupation hitherto honored and looked up to with reverent awe. It has converted the physician, the lawyer, the priest, the poet, the man of science, into its paid wage laborers."
            "The bourgeoisie has torn away from the family its sentimental veil, and has reduced the family relation to a mere money relation."
            "Let the ruling classes tremble at a Communistic revolution. The proletarians have nothing to lose but their chains. They have a world to win."
            "Workingmen of all countries unite!"
            "The bourgeoisie cannot exist without constantly revolutionizing the instruments of production, and thereby the relations of production, and with them the whole relations of society. Conservation of the old modes of production in unaltered forms, was, on the contrary, the first condition of existence for all earlier industrial classes. Constant revolutionizing of production, uninterrupted disturbance of all social conditions, everlasting uncertainty and agitation, distinguish the bourgeois epoch from all earlier ones. All fixed, fast-frozen relations, with their train of ancient and venerable prejudices and opinions, are swept away; all new-formed ones become antiquated before they can ossify. All that is solid melts into air, all that is holy is profaned, and man is at last compelled to face with sober senses his real conditions of life and his relations with his kind."
            "The bourgeoisie, by the rapid improvement of all instruments of production, by the immensely facilitated means of communication, draws all, even the most barbarian, nations into civilization. The cheap prices of its commodities are the heavy artillery with which it batters down all Chinese walls, with which it forces the barbarians' intensely obstinate hatred of foreigners to capitulate. It compels all nations, on pain of extinction, to adopt the bourgeois mode of production; it compels them to introduce what it calls civilization into their midst, i.e., to become bourgeois themselves. In one word, it creates a world after its own image."
            "Modern bourgeois society with its relations of production, of exchange, and of property, a society that has conjured up such gigantic means of production and of exchange, is like the sorcerer, who is no longer able to control the powers of the nether world whom he has called up by his spells."
            "The proletarians have nothing to loose but their chains. They have a world to win."
            "In proportion therefore, as the repulsiveness of the work increases, the wage decreases."
            "And here it becomes evident that the bourgeoisie is unfit any longer to be the ruling class in society and to impose its conditions of existence upon society as an over-riding law. It is unfit to rule because it is incompetent to assure an existence to its slave within his slavery, because it cannot help letting him sink into such a state that it has to feed him instead of being fed by him. Society can no longer live under this bourgeoisie; in other words, its existence is no longer compatible with society."
            "The essential condition for the existence, and for the sway of the bourgeois class, is the formation and augmentation of capital; the condition for capital is wage-labor. Wage-labor rests exclusively on competition between the laborers. The advance of industry, whose involuntary promoter is the bourgeoisie, replaces the isolation of the laborers, due to competition, by their revolutionary combination, due to association. The development of modern industry, therefore, cuts from under its feet the very foundation on which the bourgeoisie produces and appropriates products. What the bourgeoisie therefore produces, above all, are its own grave diggers. Its fall and the victory of the proletariat are equally inevitable."
            "But modern bourgeois private property is the final and most complete expression of the system of producing and appropriating products, that is based on class antagonisms, on the exploitation of the many by the few."
            "You are horrified at our intending to do away with private property. But in your existing society private property is already done away with for nine-tenths of the population; its existence for the few is solely due to its non-existence in the hands of those nine-tenths. You reproach us, therefore, with intending to do away with a form of property, the necessary condition for whose existence is the non-existence of any property for the immense majority of society."
            "In one word, you reproach us with intending to do away with your property. Precisely so: that is just what we intend."
            "Then the world will be for the common people, and the sounds of happiness will reach the deepest springs. Ah! Come! People of every land, how can you not be roused."
            "Communism deprives no man of the power to appropriate the products of society: all that it does is to deprive him of the power to subjugate the labor of others by means of such appropriation."
            "It has been objected, that upon the abolition of private property all work will cease, and universal laziness will overtake us."
            "When, in the course of development, class distinctions have disappeared and all production has been concentrated in the hands of a vast association of the whole nation, the public power will lose its political character. Political power, properly so called, is merely the organized power of one class for oppressing another. If the proletariat during its contest with the bourgeoisie is compelled, by the force of circumstances, to organize itself as a class, if, by means of a revolution, it makes itself the ruling class, and, as such, sweeps away by force the old conditions of production then it will, along with these conditions, have swept away the conditions for the existence of class antagonisms, and of classes generally, and will thereby have abolished its own supremacy as a class."
            "In place of the old bourgeois society with its classes and class antagonisms we shall have an association in which the free development of each is the condition for the free development of all."
            "The ruling ideas of each age have ever been the ideas of its ruling class."
            "Thus the aristocracy took their revenge by singing lampoons on their new master, and whispering in his ears sinister prophecies of coming catastrophe."
            "In this way arose feudal Socialism; half lamentation, half lampoon; half echo of the past, half menace of the future, at times by its bitter, witty and incisive criticism, striking the bourgeoisie to the very heart's core, but always ludicrous in its effects, through total incapacity to comprehend the march of modern history."
            "It has drowned the most heavenly ecstasies of religious fervour, of chivalrous enthusiasm, of Philistine sentimentalism, in the icy water of egotistical calculation. It has resolved personal worth into exchange value, and in place of numberless indefeasible chartered freedoms, it has set up that single, unconscionable freedom -- free trade. In one word, for exploitation, veiled by religious and political illusions, it has substituted naked, shameless, direct, brutal exploitation."
            "The free development of each is the condition for the free development of all."
            "The theory of Communists may be summed up in the single sentence: Abolition of private property."
            "This proposition, which, in my opinion, is destined to do for history what Darwin's theory has done for biology, we, both of us, had been gradually approaching for some years before 1845."
            "Do you charge us with wanting to stop the exploitation of children by their parents? To this crime we plead guilty."
            "These labourers, who must sell themselves piece-meal, are a commodity, like every other article of commerce, and are consequently exposed to all the vicissitudes of competition, to all the fluctuations of the market."
            "Our epoch, the epoch of the bourgeoisie, possesses, however, this distinct feature: it has simplified class antagonisms. Society as a whole is more and more splitting up into two great hostile camps, into two great classes directly facing each other — bourgeoisie and proletariat."
            "In short, the Communists everywhere support every revolutionary movement against the existing social and political order of things."
            "In all these movements they bring to the front, as the leading question in each, the property question, no matter what its degree of development at the time. "
            "Finally, they labour everywhere for the union and agreement of the democratic parties of all countries."
            "The Communists disdain to conceal their views and aims."
            "They openly declare that their ends can be attained only by the forcible overthrow of all existing social conditions."
            "Let the ruling classes tremble at a Communistic revolution. The proletarians have nothing to lose but their chains. They have a world to win."])

(def letter->tones
  {\A [:dot :dash]
   \B [:dash :dot :dot :dot]
   \C [:dash :dot :dash :dot]
   \D [:dash :dot :dot]
   \E [:dot]
   \F [:dot :dot :dash :dot]
   \G [:dash :dash :dot]
   \H [:dot :dot :dot :dot]
   \I [:dot :dot]
   \J [:dot :dash :dash :dash :dash]
   \K [:dash :dot :dash]
   \L [:dot :dash :dot :dot]
   \M [:dash :dash]
   \N [:dash :dot]
   \O [:dash :dash :dash]
   \P [:dot :dash :dash :dot]
   \Q [:dash :dash :dot :dash]
   \R [:dot :dash :dot]
   \S [:dot :dot :dot]
   \T [:dash]
   \U [:dot :dot :dash]
   \V [:dot :dot :dot :dash]
   \W [:dot :dash :dash]
   \X [:dash :dot :dot :dash]
   \Y [:dash :dot :dash :dash]
   \Z [:dash :dash :dot :dot]

   \1 [:dot :dash :dash :dash :dash]
   \2 [:dot :dot :dash :dash :dash]
   \3 [:dot :dot :dot :dash :dash]
   \4 [:dot :dot :dot :dot :dash :dash]
   \5 [:dot :dot :dot :dot :dot]
   \6 [:dash :dot :dot :dot :dot]
   \7 [:dash :dash :dot :dot :dot]
   \8 [:dash :dash :dash :dot :dot]
   \9 [:dash :dash :dash :dash :dot]
   \0 [:dash :dash :dash :dash :dash]
   \space [:silence]
   })

(def tones->letter (apply hash-map (mapcat reverse letter->tones)))

(def tone-encodings {:dot "1" :dash "111" "111" :dash "1" :dot :silence "0" "0" :silence})

(defn encode-letter [tones]
  (apply str (drop-last 1 (interleave (map tone-encodings tones)
                                      (repeat "0")))))
;; tone separated by 0
;; letter separated by 000
;; word separated by 0000000 (space)
;; so read space as 000 (0) 000

(defn encode-message [text]
  (->> text
       upper-case
       (map letter->tones)
       (map encode-letter)
       (join "000")))

(defn send-message [channel msg chunk-size]
  ;; Send headers
  (http/send! channel {:status 200} false)
  (loop [msg msg]
    (if (empty? msg)
      (http/close channel)
      (do
        (Thread/sleep 10)
        (http/send! channel (apply str (take chunk-size msg)) false)
        (recur (drop chunk-size msg))))))

(defn with-params [request]
  (let [qstring (:query-string request)
        params (split (str qstring) #"=")]
    (if (even? (count params))
      (assoc request :params (apply hash-map params))
      (assoc request :params {}))))

(defn handler [request]
  (let [request (with-params request)]
    (http/with-channel (with-params request) channel
      (let [msg (clojure.string/replace #_"dog dog" (rand-nth texts) #"[^A-Za-z1-9 ]" "")]
        (println "sending msg" msg)
        (send-message channel
                      (encode-message (clojure.string/replace msg #"[^A-Za-z1-9 ]" ""))
                      (max 1 (min 32 (Integer. (or (get-in request [:params "chunk_size"])
                                                   8)))))))))

(defonce server (atom nil))
(defn stop! [] (when-let [close-fn @server] (close-fn)))
(defn start!
  ([] (start! 9292))
  ([port]
   (stop!)
   (reset! server (http/run-server #'handler {:port port}))))

(defn -main [& args]
  (let [port (Integer. (get (System/getenv) "PORT" 9292))]
    (start! port)))

;; (client/get "http://localhost:9292" {} (fn [resp] (println resp)))

;; (loop [id 0]
;;       (when (< id 10)
;;         (timer/schedule-task (* id 200) ;; send a message every 200ms
;;                              (http/send! channel (str "message from server #" id \newline) false)) ; false => don't close after send
;;         (recur (inc id))))
;; (timer/schedule-task 10000 (http/close channel))

;;; open you browser http://127.0.0.1:9090, a new message show up every 200ms
;; (http/run-server handler {:port 9090})


;; streaming message partitioned into chunks of N characters
;; t|h|e| |q|
;; u|i|c|k| |
;; b|r|o|w|n|
;; f|o|x|j|u|

;; streaming message as ??
;; t|u|b|n|x
;; h|i|r| |
;; e|c|o|f|j
;; q|k|w|o|u

;; ** Stream message by parallelizing bits
;; Letters -> Dots/Dashes -> time-bits
;; 11101 11011 101
;; N = 5
;; [1 1 1 0 1] [1 1 0 1 1] [1 0 1]





