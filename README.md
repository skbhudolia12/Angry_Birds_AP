
# Angry Birds Game Documentation
Created by [Sakshat](https://sak-drago.github.io) & [Sarthak]()

![egg-loader-animation](https://github.com/user-attachments/assets/9acdf851-4c1e-4f5a-a282-ea23d4dda07d)

## Introduction
This project is a recreation of the popular Angry Birds game using Java, libGDX, and Box2D with a twist. The game implements physics-based gameplay, multiple levels, and features such as serialization and JUnit testing.
---
![level1_gif](https://github.com/user-attachments/assets/01dbaabd-9cb8-454f-bd9e-0609c1046b7e)

---

## Setup Instructions

### Prerequisites
- **Java**: Version 11 or higher.
- **Gradle**: For dependency management.
- **libGDX**: Game development framework (included in dependencies).
- **Box2D**: Physics engine (integrated with libGDX).

### Steps to Run the Game
>Linux distributions

1. Clone the repository from GitHub:
    ```bash
    git clone https://github.com/skbhudolia12/Angry_Birds_AP.git
    ```
2. Navigate to the project directory:
    ```bash
    cd Angry_Birds_AP-main
    ```
3. Build the project:
    ```bash
    gradle build
    ```
4. Run the game:
    ```bash
    java -jar build/libs/Angry_Bird_AP-main.jar
    ```
---
![Screenshot from 2024-11-27 01-28-20](https://github.com/user-attachments/assets/c1d5d720-9c0e-4614-ad3c-34c569c442e1)

## Features
- **Physics-based gameplay**: Powered by the Box2D engine for realistic physics interactions.  
- **Serialization**: Save and load game progress seamlessly.  
- **Dynamic collision detection**: Manage damage calculations for birds, pigs, and structures.  
- **Level progression**: Progress through multiple levels with increasing complexity.  
- **Pause and Resume**: Pause the game and resume from where you left off.  
- **Replay**: Restart levels at any time. 

## OOP Principles and Design Patterns Used

1. **Encapsulation**: 
   - Sound resources are kept private within the `GameSound` class and accessed via public methods, hiding internal details.

2. **Inheritance**: 
   - Game screens implement LibGDX’s `Screen` interface, inheriting common screen functionalities.

3. **Polymorphism**: 
   - Overridden `InputListener` methods provide custom behavior for input events, allowing different responses based on the object.

4. **Composition**: 
   - Game screens compose multiple objects (like `Stage`, `World`, etc.) to manage game logic and rendering.

5. **Singleton**: 
   - The `GameSound` class acts as a Singleton, ensuring a single instance is used for sound management across the game.

6. **Observer**: 
   - **LibGDX**'s event-driven model, where buttons and objects listen for input events, follows the Observer pattern.  
---


## Serialization
The game includes serialization to save and restore the state. The following attributes are saved:
- Current level.  
- Progress within the current level.  
- Completed levels.

The serialisation was used to:
- Determine the current score.
- How many stars to award.
- How many stars were awarded to the player.
- To save the state during pause screen.

---

## JUnit Testing

Using Headless for OpenGL simulation (The Secret Sauce to JUnit testing on OpenGL based applications), we tested core componenets of the game.
The project includes JUnit tests to validate critical functionalities:
1. **Damage Validation**: Ensures that collision-based damage calculations are correct.  
2. **Bird Iterator**: Verifies the correct iteration over available birds in a level.
3. **Bird Location**: Verifies if the bird is moving correctly or not.
4. **Bird Relocation**: Whether the game verifies the bird's relocation and uses it to determine swinging.

To run tests, execute:
```bash
gradle test
```
---
### Acknowledgments

- **[LibGDX](https://libgdx.badlogicgames.com/)**: Framework used to build the game.
- **[Box2D](https://box2d.org/)**: Physics engine used to simulate the game's physical interactions.
- **[FreeSound](https://freesound.org/)**: Sound assets provided by FreeSound.
- **[Stack Overflow](https://stackoverflow.com/)**: For providing solutions and answers to various coding challenges.
- **[GeeksforGeeks](https://www.geeksforgeeks.org/)**: For detailed tutorials and explanations of algorithms and game development concepts.
