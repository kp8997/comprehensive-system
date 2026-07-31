RADIO framework:
    Requirement
    Architecture
    Data Model
    Interface (API, UX/UI)
    Optimizations

1. Requirement: We can ask about
    ***Scope***: What is the main area we should focus on, what is the main use cases? Design facebook have many area of products (news feed, group, messaging, ads, etc.) We should ask what area we should focus on.
    ***Functional and Non-functionnal*** Requirement:
        Non-functionnal Requirement: improvement area, but not strictly required for product to be usable
            => performance, scalability, security, accessibility, etc.
        Functional Requirement: basic requirement of the product, can not work without them
            => UX (flow), UI (styling), data structure, etc.
        => ask about "Are there any areas we should include to have a detail design, and any are that need improvement or optimization beside the main flow (like color, how fast, how secure, etc.)?"
    ***Core Feature***: There are many tiny feature in a major one (New feed have posting, liking, commenting, sharing, etc., what do users often use and what do they need?)
        => Example Questions: "What kind of format can we support when posting a new post (image, text, video, gif, etc.)"
        => "Do we need to support multi language? "
    
2. Architecture:
    Server (black box):
        Care about output, what data does server give to FE
    Data access layer:
        fetching, caching, transforming, error handling. A layer that helps communicate with external sources like API, local storage, etc.
    Store layer: state management, communication between components (use context, redux, reducer, state, etc.)
    View layer: UI, user interaction. (use react, etc.)

    Other things:
        Separate of concerns:
            Consider the purpose/functionality of each component
            What data should it contain
            Reusable of that component
        Where computation should occur:
            should the work be done on the server or the client? There are tradeoffs to each approach and the deciding which depends on both the product and context.

3. Data model
    External - Server-originated data
    Internal - User-generated data:
        Data to be persisted: Data that will be sent to server later
        Ephemeral data: Temporary data like UI or temporary computation

4. Interface (API)
    External Communication:
        HTTP request
        Websocket
        Server-Sent Events
        Long-polling: client send request, when server has new update, they will response. Rarely use nowadays
        GraphQL: via HTTP, client queries only resource they need, avoid under-fetch & over-fetch
        Web RTC: client to client communicate
    Internal Communication
        props and callback
        store and dispatching actions
        publish and subscribe: pubsub via event emitter
    Components API design
        data props: id, title, value, etc.
        event/callback props: onClick, onChange, onMouseEnter, onMouseLeave, etc.
        behavior props: isOpen, isDisabled, isLoading, etc.
        styling props:  classname, style, color, etc.
        render & slot props: render, renderHeader, renderFooter, renderContent, etc.

5. Optimizations:
    General optimization / deep dive areas:
        Here's a list of topics you can dive into. Bear in mind that the importance of a topic depends on the product and some topics are entirely irrelevant to certain products.
            Performance optimizations: (ROM, RAM, CPU -> size of bundle | memory leak, cache | render optimization, computational cost, cpu usage to compute).
            Networking techniques and optimization: (HTTP request, websocket, server-sent events, long-polling, graphql, webRTC)
            User experience: (UX flow, UI design, responsive design)
            Accessibility (a11y): (screen reader, keyboard navigation, color contrast)
            Search engine optimization: (SEO, meta tags, sitemap, schema markup)
            Multilingual support: (i18n, l10n)
            Multi-device support: (responsive design, mobile-first, desktop-first)
            Security: (authentication, authorization, encryption, input validation)
    Avoid Choosing topics: Don't matter
        ***JavaScript framework***: Avoid debating React vs Vue vs Angular. The interviewer cares about more how you'd structure the app. Most modern JavaScript frameworks have similar features and most of the time you can build applications using any modern JavaScript framework.
        ***Design system / CSS framework***: Leave out framework choices such as Tailwind vs Material UI unless the question explicitly involves UI scalability or theming.
        ***General performance optimization***: Don't overemphasize generic advice like minification, image compression, or caching unless it's core to the problem being solved (e.g., rendering at scale, large data visualizations).
        ***Auxiliary product infrastructure***: Skip common topics that do not affect the core architecture, like logging, analytics, or monitoring integrations unless they directly influence user experience or system constraints.
        ***Irrelevant DevOps or CI/CD details***: Avoid going deep into deployment pipelines, Docker setups, or GitHub Actions unless it directly impacts client performance or front end release management.
        ***Tooling***: Don't focus on build tools or developer experience topics like webpack vs Vite, linting configurations, or code formatting.

    Example:
        For a photo editing tool, you might focus on:
            Performance: Efficient image processing, memory management, and rendering large canvases.
            UX: Intuitive tools, responsive controls, and real-time previews.
            Networking: Uploading and downloading large image files.

        For an e-commerce site, you might focus on:
            Performance: Fast page loads, efficient cart operations, and lazy loading.
            UX: Seamless checkout flow, clear product presentation, and responsive design.
            Security: Secure payments, authentication, and data protection.
