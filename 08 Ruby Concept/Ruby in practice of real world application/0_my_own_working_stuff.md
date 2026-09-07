	I were Responsible for those modules that owned by my squad like
		homepage/dashbaord: rearrange: widgets and tabs 
		employee onboarding flow: 
            * some tax and Ni module, 
            * Enhance some dropdown with lazy load for a ton of payroll information step for classification category - split the data on api to return the less data base on the frontend’s request to boost to speed of loading, and reduce the lagging
            * tax step of onboarding, 
            * certification upload information,
            * Medical discosure
            * Add some extending field logic for employee information like: address, bank account
		employee profile display: 
            * some information in detail about employee like: tenure working time, ATS type, employment contract type
            * Adding new contract type
            * Freemium (deprecated information) with some limitation of feature for that subscription involve some - email invitations or notification
            * terminated off boarding flow
            * Malaysia/Singapore address logic additional enhancement due to the new rule of countries
		employee listing page and their action combine with some popup modal base on the action
            * Terminate flow with information.
            * Reminded with terminated employee for managers
		report page with some report, there are many kind of reports that I could not remember all. Refactor them with new format adapt with the api
            * Sickness report
            * Review report
            * Tenure report
            * 1on1
		compliance menu
            * content uploaded document page: sidekiq to send emails, template email update, new cronjob to send email
            * Uploaded document: retrieve documents with the right type for api base on Active Record
            * Induction and content - acknowledgement of policies 
            * Certification
		Custom security
		automated flow: Add new type to shout out automatically flow
		notification:
            * celebration with show up a new modal
            * shout out type with show up a new modal
            * Reminder of terminated employee on a new modal that contains action to navigate to others things
		Task page:
            * enhance the api usage - eliminate the obsolete component of herodesign
            * integrity with new api that load a simple version of data
            * Implementation of bulk delete, bulk complete.
            * Simplify the edit / create this flow with new components
		Checklist page:
            * Fix old bugs relating to the data information display
		Tech debt:
            * Typescript enhancement
            * rspec addition in ruby for some modules
            * Snowflake resolve of hero design component on frontend
            * Review and update/remove .erb on ruby - Update specs/cucumber flow for those
            * Fix some glitches of implementation of some common library of EH
		Initiative - Additional work for module instead of team’s tasks
            * Mix panel usage unification on many modules
            * Time and formatter enhancement of migration
            * Investigate the possible of automatically replacement of deprecated usage of some library on frontend core (like redux-form)
		Feature flag interaction: implementation on both frontend and backend, toggle and set the value of the real production
		Some mobile feature
		Mobile feature adding - just some minor logic to comply the rule between web and mobile app, involve TestFlight
