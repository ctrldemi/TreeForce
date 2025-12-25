# TreeForce

TreeForce is a lightweight resource gathering plugin for PaperMC. This project is a refactored fork of TreeChopper, updated to support Minecraft 1.21.10.

## Features

* **Tree Chopping**: Break one log with an axe while sneaking to fell the entire tree.
* **Vein Mining**: Break one ore with a pickaxe while sneaking to mine the entire vein.
* **Auto-Replant**: Automatically plants a matching sapling or fungus after a tree is felled.
* **Experience Drops**: Vein-mined ores drop experience naturally.
* **Modern Compatibility**: Supports Pale Oak, Copper tools, and Nether vegetation.
* **Tool Protection**: Mining stops if the tool is about to break.

## Usage

* **Wood**: Hold an axe and sneak while breaking a log.
* **Ores**: Hold a pickaxe and sneak while breaking an ore block.

## Installation

1. Download the `TreeForce.jar` file.
2. Move the file into your server's `plugins` folder.
3. Restart your server.

## Building

Compiling requires Java 21 and Maven.

1. Clone the repository.
2. Run `mvn clean package` in the project root.
3. The compiled jar will be in the `target/` directory.

## Credits

* **Based on**: TreeChopper by beanfeed and TreeFella by V1ttorassi.
