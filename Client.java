
import java.io.*;
import java.net.*;
import java.util.*;        // required for Scanner
import org.json.simple.*;  // required for JSON encoding and decoding

public class Client {
    public static void main(String[] args) throws IOException {


        String hostName = "localhost";
        int portNumber = 12345;
        String ID= "";
        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in))
        ) {
            String userInput;
            System.out.println("--------------------");
            System.out.println("Welcome to Sheffield Hallam Messaging Application");
            displayLoginInstruction();
            while ((userInput = stdIn.readLine()) != null) {


                // Parse user and build request
                Request req;
                    Scanner sc = new Scanner(userInput);
                    String command;
                    long ts = 0;
                    String word = "";
                try {
                    command = sc.next();
                    switch (command) {
                        case "displayChannels":
                            req = new DisplayAllChannelsRequest(ID);
                            break;
                        case "myFollowing":
                            req = new DisplayChannelsSubscribedToRequest(ID);
                            break;
                        case "quit":
                            req = new QuitRequest();
                            break;
                        case "open":
                            req = new OpenRequest(ID);
                            break;
                        case "subscribe":
                            req = new SubscribeRequest(ID, sc.skip(" ").nextLine());
                            break;
                        case "unsubscribe":
                            req = new UnsubscribeRequest(ID, sc.skip(" ").nextLine());
                            break;
                        case "create":
                            ID =  sc.skip(" ").next();
                            req = new createAccountRequest(ID, sc.skip(" ").next());
                            break;
                        case "login":
                            ID =  sc.skip(" ").next();
                            req = new LogRequest(ID, sc.skip(" ").next());
                            break;
                        case "publish":
                            String body = sc.skip(" ").nextLine();
                            Message msg = new Message(body , ID, 0);
                            req = new PublishRequest(body, ID, msg);
                            break;
                        case "get":
                            ts = sc.skip(" ").nextLong();
                            req = new GetRequest(ID, ts);
                            break;
                        case "search":
                            word = sc.skip(" ").next();
                            req = new SearchMessageRequest(ID, word);
                            break;
                        default:
                            System.out.println("ILLEGAL COMMAND");
                            continue;
                    }


                } catch (NoSuchElementException e) {
                    System.out.println("ILLEGAL COMMAND");
                    continue;
                }


                // Send request to server
                out.println(req);

                // Read server response; terminate if null (i.e. server quit)
                String serverResponse;
                if ((serverResponse = in.readLine()) == null)
                    break;

                // Parse JSON response, then try to deserialize JSON
                Object json = JSONValue.parse(serverResponse);
                Response resp;

                // Try to deserialize a success response
                if (SuccessResponse.fromJSON(json) != null)
                {
                    System.out.println("Your request was successful");
                    if(command.equals("quit"))
                        System.out.println("THANKS FOR USING OUR APPLICATION SEE YOU SOON");
                    else
                        displayMenu();
                    continue;
                }

                // Try to deserialize a list of messages
                if ((resp = MessageListResponse.fromJSON(json)) != null) {
                    System.out.println("--------------------");
                    if(command.equals("get"))
                        System.out.println("LIST OF MESSAGES THAT HAVE BEEN PUBLISHED AFTER '"+ts+"': ");
                    else if(command.equals("search"))
                        System.out.println("LIST OF MESSAGES THAT CONTAIN THE WORD '"+word+"': ");
                    for (Message m : ((MessageListResponse)resp).getMessages())
                        System.out.println(m);
                    displayMenu();
                    continue;
                }


                // Try to deserialize a list of channels
                if ((resp = ChannelsResponse.fromJSON(json)) != null) {
                    System.out.println("--------------------");
                    if(command.equals("myFollowing"))
                        System.out.println("LIST OF CHANNELS THAT YOU ARE FOLLOWING:");
                    else if(command.equals("displayChannels"))
                        System.out.println("ALL THE EXISTING CHANNELS:");

                    List<String> channels = ((ChannelsResponse)resp).getChannels();
                    if(channels.size()>0)
                    {
                        for(int i=0;i<channels.size();i++)
                            System.out.println(channels.get(i));
                    }
                   else
                        System.out.println("There are no channels");
                    displayMenu();
                    continue;
                }

                // Try to deserialize an error response
                if ((resp = ErrorResponse.fromJSON(json)) != null) {
                    System.out.println(((ErrorResponse)resp).getError());
                    if(command.equals("login")||command.equals("create"))
                    {
                        displayLoginInstruction();
                    }
                    else
                        displayMenu();
                    continue;
                }

                // Not any known response
                System.out.println("PANIC: " + serverResponse +
                        " parsed as " + json);

                break;


            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
    public static void displayMenu()
    {
        System.out.println("--------------------");
        System.out.println("COMMANDS MENU:");
        System.out.println("1) open:                               this command create a channel with your user ID");
        System.out.println("2) publish + messages:                 this command will publish a message in your channel");
        System.out.println("3) displayChannels:                    this command will display all the existing channels");
        System.out.println("4) myFollowing:                        this command will display all the channels that you are subscribed to");
        System.out.println("5) subscribe + channel name:           this command will subscribe you to a specific channel");
        System.out.println("6) unsubscribe + channel name:         this command will unsubscribe you from a specific channel");
        System.out.println("7) get + timestamp:                    this command will retrieve all the messages that have been publish in all your subscribed channels based on the timestamp");
        System.out.println("8) search + word:                      this command will search for a specific word in the messages that have been publish in all your subscribed channels");
        System.out.println("9) quit:                               this command will disconnect you");
        System.out.println("--------------------");
    }
    public static void displayLoginInstruction()
    {
        System.out.println("--------------------");
        System.out.println("Login COMMANDS");
        System.out.println("1) login + ID + password               this command will log you in if you already have an account");
        System.out.println("2) create + ID + password              this command will create you an account");
    }

}