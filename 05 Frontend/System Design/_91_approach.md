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

5. Optimizations:
  