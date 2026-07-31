Consider those essential points for this section

1. ***Open question*** : System design is vague open question, there is no right answer. Interviewee is asked to do on virtual tool like excalidraw

2. ***Type*** : There are many types of system design interview questions, based on the product category we have area to focus. Like
    E-commerce: Amazon, eBay
        SEO, performance
    Chat: Messenger, Slack, Discord
        Real time communication protocols
    New feed: Facebook, Twitter, Instagram, Linkedin
        News feed, Pagination approaches, Performance, Real time
    Video streaming: Netflix, YouTube
        Streaming implementation
    Photo sharing: Instagram, Flickr, Google Photos
        Media optimization
    Collaborative apps: Google Docs, Google Sheets, Google Slides, Notion
        Conflict resolution, real time collaboration protocal, state syncing
    Email client: Gmail, Outlook, Apple mail
        App state, offline support
    Drawing tool: Figma, MS Paint, Google drawings
        Canvas manipulation Real time collaboration, state syncing
    Maps: Google map, Apple map
        Map rendering and interaction
    File storage: Google drive, Dropbox, OneDrive
        Uploading experience
    Ride sharing: Grab, GoJek, Uber
        App state, Matching algorithm, Real-time updates, Location-based services
    Music streaming: Spotify, Apple music, YouTube music
        App state, Streaming implementation
    Game: Tetrix, Snake, Tic-tac-toe, Chess, 2048, Connect Four
        Game state, Real time state syncing, Algorithm

=> basically we have: SEO, performance, real time, algorithm (matching), streaming (video, image, audio), conflict resolution, state syncing, location, canvas, offline support, app state.


3. ***UI components***
    interviewer will ask to build various UI base on the complexity we have 2 types
        Low complexity: styled button, wrapping text, badge, etc.
        High complexity: model, dropdown, date-time picker

    To have a solid design, consider those criteria:
        1. First is internal state and API between components: (parent -> children, among children)
        2. Then dive into optimization, performance, accessibility, UX, security, etc, where relevant.

    Might have requirement to write a small amout of code of the following purpose:
        1. Describe the component hierarchy
        2. Describe the shape of the component state
        3. Explain some non-trivial logic within the component

        e.g

        <ImageCarousel
            images={...}
            onPrev={...}
            onNext={...}
            layout="horizontal">
            <ImageCarouselImage style={...} />
            <ImageThumbnail onClick={...} />
        </ImageCarousel>

4. ***Customizing Theming***
    Expectation to design a component allow for other developer to customize:
        theming: layout, styling, etc.
        logic: functions, data, etc.

5. ***Case Study***: Examples of UI components asked during front end system design interviews
    Design an autocomplete component
    Design a dropdown menu component
    Design an image carousel
    Design an embeddable poll widget
    Design a rich text editor
    Design a modal component
    Design a data table with sorting and pagination
    Design a date picker
    Design a multiselect component


-------------------------------------------------

### Summary ###

To have a solid design, consider those criteria:
    1. Open question - open answer do on virtual whiteboard
    2. Type of product -> type of design
    3. Component design
    4. Customization of component
    5. Case study
