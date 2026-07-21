1. Component-Based & Declarative Architecture
    Encapsulation: UI is broken into isolated, reusable pieces called Components. Each component manages its own structure, logic, and style.
    Declarative UI: You declare what the UI should look like for any given state value. React takes care of imperatively mutating the browser’s DOM under the hood.

2. JSX (JavaScript XML) Abstraction
    Syntactic Sugar: JSX is a syntax extension that allows writing HTML-like markup directly inside JavaScript.
    Transformation: Web browsers do not understand JSX natively. Build tools (Babel/SWC) compile JSX into pure JavaScript function calls (React.createElement or modern jsx() runtime calls).

        // JSX Input
        <button className="primary" onClick={handleClick}>Click Me</button>
        // Compiled JavaScript Output
        import { jsx as _jsx } from "react/jsx-runtime";
        _jsx("button", { className: "primary", onClick: handleClick, children: "Click Me" });

3. Props & Unidirectional Data Flow
    Props (Properties): Immutable input parameters passed from a parent component down to a child component. A component must never modify its own props.
    One-Way Data Flow: Data flows downward in a single direction (Parent -> Child). To pass data back upward, parents pass callback functions down as props for children to trigger.

4. Virtual DOM & Reconciliation Engine
    Virtual DOM (VDOM): An in-memory JavaScript object tree that mirrors the real browser DOM elements.
    Diffing Algorithm: When state updates, React creates a new VDOM tree and compares it against the previous VDOM tree.
    Reconciliation (Fiber Engine): React calculates the minimal set of structural and visual differences between trees and batch-updates only those specific elements in the real browser DOM.

5. State & Hooks (Functional Paradigm)
    State: Mutable internal memory owned and preserved by a component between renders. When state updates, React schedules a re-render of that component.
    React Hooks: Standardized APIs (useState, useEffect, useContext, useReducer) introduced to compose stateful logic and side effects directly inside functional components without writing ES6 classes.

6. Component Lifecycle & Side Effects
    Every React component instance traverses three core operational phases:
    Phase	Description	Key Hook Equivalent
    Mounting	Element is initialized and inserted into the browser DOM tree.	useEffect(() => { ... }, [])
    Updating	Component re-renders due to changes in internal state or incoming props.	useEffect(() => { ... }, [dep])
    Unmounting	Element is destroyed and removed from the browser DOM tree.	useEffect(() => return () => { /* cleanup */ }, [])

7. Concurrent Rendering & Scheduling Engine
    Interruptible Rendering: Modern React (v18+) splits long rendering tasks into smaller chunks using a prioritized fiber scheduler.
    Non-Blocking UI: React can pause background rendering tasks to process user interactions (like typing in an input field) immediately, preserving 60 FPS performance.
