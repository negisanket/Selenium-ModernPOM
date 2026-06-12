# Selenium MCP Bridge Server

This is a Node.js-based MCP (Model Context Protocol) server that acts as a bridge between an AI agent and your Selenium-ModernPOM framework.

## Capabilities
- **`run_maven_test`**: Triggers a TestNG suite run via Maven.
- **`get_test_artifacts`**: Lists screenshots from failed test runs.
- **`read_page_object`**: Allows the AI to read your Page Object classes to understand locators.

## Prerequisites
- Node.js v18+ (v24.16.0 detected)
- Maven installed and in PATH
- The parent Selenium project must be correctly configured.

## Setup
1. Navigate to this directory:
   ```bash
   cd mcp-bridge
   ```
2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Server
The server uses **Stdio Transport**, which is the standard for MCP.

### For Claude Desktop (Configuration)
Add this to your `claude_desktop_config.json`:
```json
{
  "mcpServers": {
    "selenium-bridge": {
      "command": "node",
      "args": ["C:/Users/SanLu/Documents/Idea Proj/Selenium-ModernPOM/mcp-bridge/index.js"]
    }
  }
}
```

### Manual Execution (For Debugging)
```bash
node index.js
```
*Note: The server will wait for input on stdin. You can type `{"jsonrpc": "2.0", "method": "list_tools", "id": 1}` to test if it's responding.*
