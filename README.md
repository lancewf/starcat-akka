# starcat-akka
This project is a scala akka version of starcat. Using Actors and futures this turns the starcat system into a reactive system.

Starcat is a framework which was created by Dr. Joseph Lewis to provide the basic
functionality of the Copycat program; it allows other domains to utilize the essential
functionality of Copycat. “Emergent representation is a key feature of Starcat’s functioning
and adaptive behavior is the desired result that it supports.” Unlike Copycat, the Starcat
framework is not specific to any domain; it allows custom domain-specific modules to be
added on top of the main Copycat engines. Lewis states that the Madcat and Copycat
systems have “two important limitations… their inability to adapt to novel situations and
their lack of true autonomy.” Aside from not having domain-specificity, Starcat also adds
value to existing CAS in that it “is an open-ended architecture for computational systems that
autonomously adapt their behavior to continuously changing environments.” Starcat’s
ability to allow for autonomous systems presents a new niche in the field of AI.
