<!--
SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>

All rights reserved.
-->

# TBIAL

This project is used as the starting point in this practical course.
More information is on [Uni2Work](https://uni2work.ifi.lmu.de/course/S22/IfI/SWEP).

## Running the application
- If you've already cloned the project and started it once, before you start it again, please
delete the contents of /database.  
 
- The project is a standard Maven project.
To run it from the command line, type `mvn`,
which downloads dependencies, builds the application, and starts it.
If it is not opened automatically,
open [http://localhost:8080](http://localhost:8080) in your browser.

You can also import the project in your IDE
and start it from there (cf. the slides for more information).

## Project structure

- `MainLayout.java` in `de.lmu.ifi.sosy.tbial.views` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `de.lmu.ifi.sosy.tbial` contains the server-side Java views of your
  application.
- `themes` folder in `frontend/` contains the custom CSS styles.
- `Application` in package `de.lmu.ifi.sosy.tbial` contains the main method

## Useful Maven Commands

- `mvn`: Build and run the project.
- `mvn compile`: Compile the project without launching it.
- `mvn clean`: Remove the compiled files by deleting the `target/` directory.
  The next build will start from a clean state.
- `mvn verify`: Run all tests.
- `mvn spotless:check`: Check if the code conforms to Google Java Format (v1.15.0).
- `mvn spotless:apply`: Reformat the code according to Google Java Format (v1.15.0). It is however
  recommended that you install the corresponding plugin in your IDE to reformat the code
  automatically.
- `mvn spotbugs:check`: Look for bugs in the code. The project must be compiled beforehand.
- `mvn forbiddenapis:check`: Check the code against a list of forbidden API signatures.
  The project must be compiled beforehand.

## Useful links for Vaadin

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified
  at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Search UI components and their usage examples
  at [vaadin.com/components](https://vaadin.com/components).
- View use case applications that demonstrate Vaadin capabilities
  at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Discover Vaadin's set of CSS utility classes that enable building any UI without custom CSS in
  the [docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes).
- Find a collection of solutions to common use cases
  in [Vaadin Cookbook](https://cookbook.vaadin.com/).
- Find Add-ons at [vaadin.com/directory](https://vaadin.com/directory).

## App architecture
[![](https://mermaid.ink/img/pako:eNpNUk1vwjAM_StWpE1MKvwADpPYygFpkybYduolTU3xliaVk4IQ4r_PzUpLT4773vPzx0UZX6Faqpp1e4DPvHAg38MDfHSMcCQ8gfU1mcX_j9XsW1JPMJ8_w0uK34Vvn0bamApgfGcr0DZ4oKa12KCLEFIItW5wEIaRu3ERWZtI3kGJ8YTokh483slmQAtcwBuFiA45g6_NKNC1lY4YBq_3_pLh1QjcIR_JYLgzNjkaKmy9xQGXjcRVSZbi-ZaHVXI7PAGjtKNdBTVGiAckBjGkR_aefdPnYYutDxQ9n29ek8HX2aA0zXNCAgXQSQ-C79iIxTWzZzhIRUuuzoAxtN4FKmXCe8-jiOGuAr-XXVjZiuzVlz9o4m1Qr6l4PptqTfV3-ihj6ntijEzYv3oPAzVP1PUsl1SpQ29cZapBbjRVclaXHlYo6bnBQi0lrDT_FqpwV8H972td9TXVci-ngpnSXfS7szNqGbnDGygnLSfaDKjrHw4_37Y)](https://mermaid.live/edit#pako:eNpNUk1vwjAM_StWpE1MKvwADpPYygFpkybYduolTU3xliaVk4IQ4r_PzUpLT4773vPzx0UZX6Faqpp1e4DPvHAg38MDfHSMcCQ8gfU1mcX_j9XsW1JPMJ8_w0uK34Vvn0bamApgfGcr0DZ4oKa12KCLEFIItW5wEIaRu3ERWZtI3kGJ8YTokh483slmQAtcwBuFiA45g6_NKNC1lY4YBq_3_pLh1QjcIR_JYLgzNjkaKmy9xQGXjcRVSZbi-ZaHVXI7PAGjtKNdBTVGiAckBjGkR_aefdPnYYutDxQ9n29ek8HX2aA0zXNCAgXQSQ-C79iIxTWzZzhIRUuuzoAxtN4FKmXCe8-jiOGuAr-XXVjZiuzVlz9o4m1Qr6l4PptqTfV3-ihj6ntijEzYv3oPAzVP1PUsl1SpQ29cZapBbjRVclaXHlYo6bnBQi0lrDT_FqpwV8H972td9TXVci-ngpnSXfS7szNqGbnDGygnLSfaDKjrHw4_37Y)
- **View**: Implement pure view logic, i.e. _GameBoardView_, _LobbyView_ etc.
- **ViewModel**: Interaction between Views & ViewModels, i.e. _Listener_, _UI updates_, etc.
Could also implement very simple game logic.
- **Services**: Implement main game logic, i.e. _RoleService_,
_AbilityService_, _ActionService_ etc. They get their data
from the _Repository_.
- **Repository**: A data source. Error handling, responsible
for CRUD of all core objects.

## Licenses

The license of the skeleton is defined in [LICENSE.txt](LICENSE.txt).

Everything used in the project must be under an open-source license.
You may not use Vaadin's proprietary offerings like Collaboration Engine.
