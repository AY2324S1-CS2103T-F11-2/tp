---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# WedLog Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/Main.java) and [`MainApp`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `guest delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("guest delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `guest delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `GuestCommandParser` and `GuestDeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a general parser that matches the command (e.g., `GuestCommandParser`).
2. In turn, the general parser creates a parser that matches the command (e.g., `GuestDeleteCommandParser`) and uses it to parse the command.
3. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `GuestDeleteCommand`) which is executed by the `LogicManager`.
4. The command can communicate with the `Model` when it is executed (e.g. to delete a guest).
5. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates a `GuestCommandParser` or `VendorCommandParser` depending on the command. This class then creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `GuestAddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `GuestAddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `GuestAddCommandParser`, `VendorDeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data e.g., all `Guest` objects (which are contained in a `UniqueGuestList` object).
* stores the currently 'selected' `Guest` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Guest>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2324S1-CS2103T-F11-2/tp/tree/master/src/main/java/wedlog/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `wedlog.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Edit feature
The edit feature allows users to edit any fields for a guest or vendor in WedLog (i.e., `Name`, `Phone`, `Email`, 
`Address`, `Tags`, and `Rsvp Status`, `Dietary Requirements` and `Table Number` for Guests). The edit feature will 
replace the existing values in these fields with the new values inputted by the user. It makes use of the `Index` of 
the person in the currently displayed list to identify the person to be edited. The feature is implemented through the 
classes `GuestEditCommand` and `VendorEditCommand`. 

Given below is an example usage scenario and how the edit mechanism behaves at each step.

Step 1. The user launches the application. WedLog shows all guests and vendors in their respective lists.

Step 2. The user executes `xyz edit 5 n/John`, where `xyz` is either `guest` or `vendor`. This allows the user to edit the name of the fifth
guest or vendor to `John`. For illustration purposes, we shall assume that `xyz` is `guest` for the following steps. To understand
the usage scenario for `vendor edit`, simply replace all `Guest` keywords in class and method names with `Vendor`.

Step 3. `GuestCommandParser` creates a `GuestEditCommandParser` object and calls `GuestEditCommandParser#parse` to parse the user input.
`GuestEditCommandParser#parse` parses the `Index` of the guest to be edited.

Step 4. `GuestEditCommandParser#parse` creates an `EditGuestDescriptor`. It calls upon `ParserUtil#parseXyz`, where `xyz` is the 
field being edited, to check the validity of the user input and convert it into field objects (e.g. string representing a new name into a `Name` object).
If input is valid, `#parse` then assigns the created field objects to the created `EditGuestDescriptor`.

Step 5. Lastly, `GuestEditCommandParser#parse` creates a `GuestEditCommand` using the `Index` and `editGuestDescriptor` obtained in
Steps 3 and 4. It returns the created `GuestEditCommand` to `LogicManager`, which calls the `#execute` method on the `GuestEditCommand`.

Step 6. `GuestEditCommand#execute` creates a new guest by combining fields from the existing guest object referenced by `Index` and
the new field values specified by the user. By calling on `Model#setGuest`, it then replaces the existing guest in WedLog with the newly created guest.


### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how the undo operation works:

<puml src="diagrams/UndoSequenceDiagram.puml" alt="UndoSequenceDiagram" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:
A bride or groom who
* Is tech-savvy and is comfortable using CLI
* Can type fast
* Prefers working on a desktop
* Wants to plan their own wedding
* Has a partner who is uninvolved in the planning process
* Values efficiency and having a consolidated wedding planning platform

**Value proposition**: consolidate all information related to wedding guests and vendors into 1 platform for 
streamlined planning.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                         | I want to …​                                          | So that I can…​                                                |
|----------|---------------------------------|-------------------------------------------------------|----------------------------------------------------------------|
| `* * *`  | new user                        | add new guests with name and contact                  | keep track of the people whom I invited to the wedding         |
| `* * *`  | new user                        | remove existing guests                                | remove guests I erroneously added                              |
| `* * *`  | new user                        | record down dietary requirements for guests           | cater correct types of meals for my guests                     |
| `* * *`  | new user                        | update RSVP status of a guest                         | track who is coming                                            |
| `* * *`  | new user                        | save data into local storage                          | keep my data even after I exit the app                         |
| `* * *`  | new user                        | retrieve data from local storage                      | access past data that I have inputed                           |
| `* * *`  | user liaising with many vendors | add new vendor with name and contact                  | keep track of which vendors I am currently in contact with     |
| `* * *`  | user liaising with many vendors | remove existing vendors                               | remove vendors I erroneously added                             |
| `* *`    | user with many guests           | view how many guests have RSVP'd                      | know how many guests are confirmed to be coming                |
| `* *`    | user with many guests           | add a note for a guest                                | take note of any miscellaneous information about a guest       |
| `* *`    | user with many guests           | view a specific guest's details                       | understand the arrangements I've made for a particular guest   |
| `* *`    | user with many guests           | view the total number of each type of diet            | cater the appropriate number and types of meals                |
| `* *`    | user with many guests           | filter guests with dietary restrictions               | cater alternate meals for them                                 |
| `* *`    | user with many guests           | assign a guest to a table number                      | keep track of seating arrangements                             |
| `* *`    | user with many guests           | filter guests by table                                | see who is sitting at each table                               |
| `* *`    | user with many guests           | add guests to a group                                 | know which group a guest belongs to                            |
| `* *`    | user with many guests           | filter guests by groups (eg family, friends)          | access and manage relevant information for each group          |
| `*`      | financially savvy user          | track my total expenses                               | stay within budget                                             |
| `*`      | financially savvy user          | keep track of the costs associated with each vendor   | track how much I have spent on the wedding                     |
| `*`      | financially savvy user          | record gift registry                                  | take note of which gifts are already bought, and by who        |
| `*`      | financially savvy user          | keep track of red packets received from guests        | keep future references on gift exchange                        |
| `*`      | financially savvy user          | view total amount I collected from wedding presents   | know how much cash I have                                      |
| `*`      | user finding a wedding location | record pros and cons of possible locations            | choose the best location                                       |
| `*`      | task-oriented user              | add tasks and track roadmap                           | keep track of the wedding planning progress                    |
| `*`      | task-oriented user              | get reminders to do tasks when deadlines are nearing  | not miss important deadlines                                   |
| `*`      | task-oriented user              | assign tasks and deadlines to guests                  | keep track of when I need to follow-up with them               |
| `*`      | task-oriented user              | assign tasks and deadlines to vendors                 | keep track of when I need to follow-up with them               |
| `*`      | task-oriented user              | view all the deadlines that have passed               | identify the actions that I need to prioritise                 |
| `*`      | task-oriented user              | view countdown to wedding                             | know the time I have left till the wedding                     |
| `*`      | user planning my reception      | plan the flow of events during the reception          | know when to do what                                           |
| `*`      | experienced user                | view most recent commands                             | look back on what was recorded previously                      |
| `*`      | experienced user                | undo the last command                                 | undo accidental or erroneous commands                          |
| `*`      | experienced user                | import data from a csv file format onto this platform | easily transfer existing information from other sources        |
| `*`      | experienced user                | export data into an excel format                      | easily send data to vendors                                    |
| `*`      | experienced user                | share my address book with another user               | plan the wedding together with my partner                      |
| `*`      | experienced user                | add custom fields for guests                          | keep track of miscellaneous information specific to my wedding |


### Use cases
(For all use cases below, the **System** is the `WedLog` and the **Actor** is the `user`, unless specified otherwise)

<box>Use case: UC1 - Add a guest</box>

**MSS:**
1. User provides guest details.
2. WedLog adds the guest to the guest list.
<br>Use case ends.

**Extensions:**
<br>1a. The given input format is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1a2. User provides guest details in a different format.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Steps 1a1-1a2 are repeated until the User input format is valid.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes from step 2.</span>
<br>1b. The guest list has reached maximum capacity.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1b1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case ends.</span>
<br>1c. The guest already exists in the guest list.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1c1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case ends.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC2 - Delete a guest</box>

**MSS:**
1. User requests to list all guests
2. WedLog shows a list of guests
3. User requests to delete a specific guest in the list via its index
4. WedLog deletes the guest
   <br>Use case ends.

**Extensions:**
<br>3a. The given index is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a2. User requests to delete the guest using a different index</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Steps 3a1-3a2 are repeated until the User provides a valid index.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes from step 4.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC3 - View all guests</box>

**MSS:**
1. User requests to list all guests
2. WedLog displays a list of guests
<br>Use case ends.

**Extensions:**
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC4 - View a specific guest</box>

**MSS:**
1. User requests to list all guests
2. WedLog displays a list of guests
3. User requests to view a specific guest in the list
4. WedLog displays the guest’s details
<br>Use case ends.

**Extensions:**
<br>3a. The given index is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes at step 2.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC5 - Add a vendor</box>

**MSS:**
1. User provides vendor details.
2. WedLog adds the vendor to the vendor list.
<br>Use case ends.

**Extensions:**
<br>1a. The given input format is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1a2. User provides vendor details in a different format.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Steps 1a1-1a2 are repeated until the User input format is valid.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes from step 2.</span>
<br>1b. The vendor list has reached maximum capacity.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1b1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case ends.</span>
<br>1c. The guest already exists in the vendor list.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;1c1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case ends.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC6 - Delete a vendor</box>

**MSS:**
1. User requests to list all vendor
2. WedLog shows a list of vendors
3. User requests to delete a specific vendor in the list via its index
4. WedLog deletes the vendor
<br>Use case ends.

**Extensions:**
<br>3a. The given index is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a2. User requests to delete the vendor using a different index</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Steps 3a1-3a2 are repeated until the User provides a valid index.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes from step 4.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC7 - View all vendors</box>

**MSS:**
1. User requests to list all vendors
2. WedLog displays a list of vendors
<br>Use case ends.

**Extensions:**
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

<box>Use case: UC8 - View a specific vendor</box>

**MSS:**
1. User requests to list all vendors
2. WedLog displays a list of vendors
3. User requests to view a specific vendor in the list
4. WedLog displays the vendor’s details
<br>Use case ends.

**Extensions:**
<br>3a. The given index is invalid.
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;3a1. WedLog shows an error message.</span>
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;Use case resumes at step 2.</span>
<br>*a. At any time, user inputs an invalid command/syntax
<br><span>&nbsp;&nbsp;&nbsp;&nbsp;*a1. WedLog shows an error message.</span>

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse. 
4. Should work without internet connection 
5. For any command, the system should respond within 2 seconds. 
6. Should be usable for colour-blind users. 
7. Should provide comprehensive documentation for users to learn how to use the command-line interface effectively. 
8. Should provide clear and user-friendly error messages, guiding users on how to rectify issues. 
9. Should log errors for analysis and debugging.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **CLI**: Command Line Interface
* **GUI**: Graphical User Interface
* **JSON**: [JavaScript Object Notation](https://www.json.org/json-en.html)
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the `.jar` file and copy into an empty folder

   1. Double-click the `.jar` file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the `.jar` file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

Coming soon

### Saving data

Coming soon
