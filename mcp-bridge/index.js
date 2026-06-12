import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import { exec } from "child_process";
import { promisify } from "util";
import fs from "fs/promises";
import path from "path";
import { fileURLToPath } from "url";

const execAsync = promisify(exec);
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const PROJECT_ROOT = path.resolve(__dirname, "..");

const server = new Server(
  {
    name: "selenium-bridge",
    version: "1.0.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

/**
 * Tool definitions
 */
server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [
      {
        name: "run_maven_test",
        description: "Executes a Maven test suite or command in the Selenium project.",
        inputSchema: {
          type: "object",
          properties: {
            suite: {
              type: "string",
              description: "The TestNG suite file to run (e.g., testng.xml, testng-smoke.xml)",
            },
            browser: {
              type: "string",
              description: "The browser to use (chrome, firefox, edge)",
            },
          },
        },
      },
      {
        name: "get_test_artifacts",
        description: "Retrieves a list of screenshots and logs from the latest test run.",
        inputSchema: {
          type: "object",
          properties: {},
        },
      },
      {
        name: "read_page_object",
        description: "Reads the content of a specific Page Object file to understand locators.",
        inputSchema: {
          type: "object",
          properties: {
            fileName: {
              type: "string",
              description: "Name of the Page Object file (e.g., LoginPage.java)",
            },
          },
          required: ["fileName"],
        },
      },
      {
        name: "execute_framework_action",
        description: "Performs a direct action using DriverFactory and ElementUtils building blocks.",
        inputSchema: {
          type: "object",
          properties: {
            browser: {
              type: "string",
              description: "The browser to use (chrome, firefox)",
            },
            action: {
              type: "string",
              enum: ["navigate", "click", "type", "gettext"],
              description: "The framework action to perform",
            },
            target: {
              type: "string",
              description: "The target URL or the Page Object element (e.g., LoginPage.usernameField)",
            },
            value: {
              type: "string",
              description: "The text to type (if action is 'type')",
            },
          },
          required: ["action", "target"],
        },
      },
    ],
  };
});

/**
 * Tool logic handlers
 */
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

  try {
    switch (name) {
      case "run_maven_test": {
        const suite = args?.suite || "testng.xml";
        const browser = args?.browser || "chrome";
        const cmd = `mvn clean test -DsuiteXmlFile=src/test/resources/${suite} -Dbrowser=${browser}`;
        
        console.error(`Executing: ${cmd}`);
        const { stdout, stderr } = await execAsync(cmd, { cwd: PROJECT_ROOT });
        
        return {
          content: [
            {
              type: "text",
              text: `STDOUT:\n${stdout}\n\nSTDERR:\n${stderr}`,
            },
          ],
        };
      }

      case "get_test_artifacts": {
        const screenshotDir = path.join(PROJECT_ROOT, "build", "screenshots");
        try {
          const files = await fs.readdir(screenshotDir);
          return {
            content: [
              {
                type: "text",
                text: `Available Screenshots in ${screenshotDir}:\n${files.join("\n")}`,
              },
            ],
          };
        } catch (err) {
          return {
            content: [
              {
                type: "text",
                text: `Could not read screenshots directory: ${err.message}`,
              },
            ],
          };
        }
      }

      case "read_page_object": {
        const filePath = path.join(PROJECT_ROOT, "src", "main", "java", "com", "modern", "automation", "pages", args.fileName);
        const content = await fs.readFile(filePath, "utf-8");
        return {
          content: [
            {
              type: "text",
              text: content,
            },
          ],
        };
      }

      case "execute_framework_action": {
        const browser = args?.browser || "chrome";
        const action = args.action;
        const target = args.target;
        const value = args?.value || "";

        const cmd = `mvn compile exec:java -Dexec.mainClass="com.modern.automation.utils.McpActionRunner" -Dexec.args="${browser} ${action} ${target} ${value}"`;
        
        console.error(`Executing Framework Action: ${cmd}`);
        const { stdout, stderr } = await execAsync(cmd, { cwd: PROJECT_ROOT });
        
        return {
          content: [
            {
              type: "text",
              text: `Action Outcome:\n${stdout}\n${stderr}`,
            },
          ],
        };
      }

      default:
        throw new Error(`Unknown tool: ${name}`);
    }
  } catch (error) {
    return {
      isError: true,
      content: [
        {
          type: "text",
          text: error.message,
        },
      ],
    };
  }
});

/**
 * Start the server using stdio transport
 */
async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("Selenium MCP Bridge Server running on stdio");
}

main().catch((error) => {
  console.error("Server error:", error);
  process.exit(1);
});
