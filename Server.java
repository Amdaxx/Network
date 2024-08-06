import java.net.*;
import java.io.*;
import java.util.*;        // required for List and Scanner
import org.json.simple.*;  // required for JSON encoding and decoding
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
public class Server {
    private   static  List<String> channels = new ArrayList<String>();
    public  void retrieveChannels()
    {
        File file = new File("channels.txt");
        if(file.exists())
        {
            try {
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    channels.add(data);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Could not open the channels file");
                e.printStackTrace();
            }
        }
    }

    static class Clock {
        private long t;

        public Clock()
        {
            File f = new File("tValue.txt");
            if(f.exists() && !f.isDirectory())
            {
                try {
                    File myObj = new File("tValue.txt");
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        t = Long.parseLong(data);
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
            else
                t = 0;
        }

        public synchronized void update()
        {
            try {
                FileWriter myWriter = new FileWriter("tValue.txt");
                myWriter.write(Long.toString(t));
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // tick the clock and return the current time
        public synchronized long tick() { return ++t; }

    }


    static class ClientHandler extends Thread {

        // shared logical clock
        private static Clock clock = new Clock();

        // number of messages that were read by this client already
        private int read;

        // login name; null if not set
        private String login;

        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            client = socket;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            read = 0;
            login = null;
        }

        public synchronized void createNewChannel(String channelName)
        {
            try {
                File file2 = new File(channelName + "MSG");
                file2.createNewFile();
                BufferedWriter myWriter2 = new BufferedWriter(new FileWriter("channels.txt", true));
                myWriter2.write(channelName+"\n");
                myWriter2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // tick the clock and record the current time stamp
                    long ts = clock.tick();
                    clock.update();
                    // logging request (+ login if possible) to server console
                    if (login != null)
                        System.out.println(inputLine);
                    else
                        System.out.println(inputLine);




                    // parse request, then try to deserialize JSON
                    Object json = JSONValue.parse(inputLine);
                    Request req;


                    if (login != null &&
                            (req = PublishRequest.fromJSON(json)) != null) {

                        Message message = ((PublishRequest)req).getMsg();
                        message.setTimestamp(ts);
                        synchronized (ClientHandler.class) {
                            File file = new File(login+"MSG");
                            if(file.exists())
                            {
                                try {
                                    BufferedWriter myWriter = new BufferedWriter(new FileWriter(login+"MSG", true));
                                    myWriter.write(message.getAuthor()+"\n");
                                    myWriter.write(ts+"\n");
                                    myWriter.write(message.getBody()+"\n");
                                    myWriter.close();
                                    out.println(new SuccessResponse());
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }
                            }
                            else
                                out.println(new ErrorResponse("There is no such channel"));
                        }
                        continue;
                    }

                    // get request
                    if (login != null && (req = GetRequest.fromJSON(json)) != null) {
                        String ID = ((GetRequest)req).getID();
                        long WHEN = ((GetRequest)req).getWhen();
                        synchronized (ClientHandler.class)
                        {
                            List<Message> msgs = new ArrayList<Message>();

                            try {
                                File myObj = new File(login);
                                Scanner myReader = new Scanner(myObj);
                                while (myReader.hasNextLine()) {
                                    String data = myReader.nextLine();
                                    File channel = new File(data+"MSG");
                                    Scanner reader = new Scanner(channel);
                                    int count=0;
                                    while(reader.hasNext())
                                    {
                                     count++;
                                     String d = reader.nextLine();
                                    }

                                    int nooMessages = count/3;
                                    int messageRead = 0;
                                    Scanner reader1 = new Scanner(channel);
                                    int count2 = 0;
                                    String message ="";
                                    long when = 0;
                                    String chan = "";
                                    while(reader1.hasNext())
                                    {
                                        count2++;
                                        if(count2 == 1 + messageRead*3)
                                            chan = reader1.nextLine();
                                        if(count2 == 2 + messageRead*3)
                                            when = Long.parseLong(reader1.nextLine());
                                        if(count2 == 3 + messageRead*3)
                                        {
                                            message = reader1.nextLine();
                                            if(when>=WHEN)
                                                msgs.add(new Message(message, chan, when));
                                            messageRead++;
                                        }

                                    }
                                }
                                myReader.close();
                                Collections.sort(msgs, new Comparator<Message>() {
                                    @Override
                                    public int compare(Message o1, Message o2) {
                                        return Long.valueOf(o1.getTimestamp()).compareTo(Long.valueOf(o2.getTimestamp()));
                                    }
                                });
                                out.println(new MessageListResponse(msgs));
                                continue;
                            } catch (FileNotFoundException e) {
                                System.out.println("Could not open the channels file");
                                e.printStackTrace();
                            }
                            out.println(new MessageListResponse(msgs));
                            continue;
                        }
                    }


                    //Search request
                    if (login != null && (req = SearchMessageRequest.fromJSON(json)) != null) {
                        String ID = ((SearchMessageRequest)req).getID();
                        String word = ((SearchMessageRequest)req).getWord();
                        synchronized (ClientHandler.class)
                        {
                            List<Message> msgs = new ArrayList<Message>();

                            try {
                                File myObj = new File(login);
                                Scanner myReader = new Scanner(myObj);
                                while (myReader.hasNextLine()) {
                                    String data = myReader.nextLine();
                                    File channel = new File(data+"MSG");
                                    Scanner reader = new Scanner(channel);
                                    int count=0;
                                    while(reader.hasNext())
                                    {
                                        count++;
                                        String d = reader.nextLine();
                                    }

                                    int nooMessages = count/3;
                                    int messageRead = 0;
                                    Scanner reader1 = new Scanner(channel);
                                    int count2 = 0;
                                    String message ="";
                                    long when = 0;
                                    String chan = "";
                                    while(reader1.hasNext())
                                    {
                                        count2++;
                                        if(count2 == 1 + messageRead*3)
                                            chan = reader1.nextLine();
                                        if(count2 == 2 + messageRead*3)
                                            when = Long.parseLong(reader1.nextLine());
                                        if(count2 == 3 + messageRead*3)
                                        {
                                            message = reader1.nextLine();
                                            boolean contains = message.contains(word);
                                            if(contains)
                                                msgs.add(new Message(message, chan, when));
                                            messageRead++;
                                        }
                                    }
                                }
                                myReader.close();
                                out.println(new MessageListResponse(msgs));
                                continue;
                            } catch (FileNotFoundException e) {
                                System.out.println("Could not open the channels file");
                                e.printStackTrace();
                            }
                            Collections.sort(msgs, new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return Long.valueOf(o1.getTimestamp()).compareTo(Long.valueOf(o2.getTimestamp()));
                                }
                            });
                            out.println(new MessageListResponse(msgs));
                            continue;
                        }
                    }



                    // displayAllChannels request? Must be logged in
                    if (login != null && DisplayAllChannelsRequest.fromJSON(json) != null) {
                        List<String> channels = new ArrayList<>();
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            try {
                                File myObj = new File("channels.txt");
                                Scanner myReader = new Scanner(myObj);
                                while (myReader.hasNextLine()) {
                                    String data = myReader.nextLine();
                                    channels.add(data);
                                }
                                myReader.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("An error occurred.");
                                e.printStackTrace();
                            }
                        }
                        // response: list of unread messages
                        out.println(new ChannelsResponse(channels));
                        continue;
                    }

                    //  DisplayChannelsSubscribedTo request? Must be logged in
                    if (login != null && DisplayChannelsSubscribedToRequest.fromJSON(json) != null) {
                        List<String> channels = new ArrayList<>();
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            try {
                                File myObj = new File(login);
                                Scanner myReader = new Scanner(myObj);
                                while (myReader.hasNextLine()) {
                                    String data = myReader.nextLine();
                                    channels.add(data);
                                }
                                myReader.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("An error occurred.");
                                e.printStackTrace();
                            }
                        }
                        // response: list of unread messages
                        out.println(new ChannelsResponse(channels));
                        continue;
                    }

                    // open request? Must be logged in
                    if (login != null &&
                            (req = OpenRequest.fromJSON(json)) != null) {
                        String channelName = ((OpenRequest)req).getID();
                        synchronized (ClientHandler.class) {
                            if (channels.size() != 0) {
                                boolean check = false;
                                for (int i = 0; i < channels.size(); i++)
                                {
                                    String cname = channels.get(i);
                                    if(cname.equals(channelName))
                                    {
                                        check = true;
                                        break;
                                    }
                                }
                                    if (check == true)
                                    {
                                        out.println(new ErrorResponse("This channel name already exists"));
                                    }
                                    else
                                    {
                                        try {
                                            BufferedWriter myWriter = new BufferedWriter(new FileWriter(login, true));
                                            myWriter.write(login+"\n");
                                            myWriter.close();
                                        } catch (IOException e) {
                                            System.out.println("An error occurred.");
                                            e.printStackTrace();
                                        }
                                        createNewChannel(login);
                                        channels.add(channelName);
                                        out.println(new SuccessResponse());
                                    }
                                }
                            else
                            {
                                try {
                                    BufferedWriter myWriter = new BufferedWriter(new FileWriter(login, true));
                                    myWriter.write(login+"\n");
                                    myWriter.close();
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }
                                createNewChannel(channelName);
                                channels.add(channelName);
                                out.println(new SuccessResponse());
                            }
                        }
                        continue;
                    }

                    // sub request? Must be logged in
                    if (login != null &&
                            (req = SubscribeRequest.fromJSON(json)) != null) {
                        String channelName = ((SubscribeRequest)req).getChannelName();
                        boolean checkIfChannelExists = false;
                        boolean checkIfAlrdySub= false;
                        synchronized (ClientHandler.class)
                        {
                            for(int i=0; i<channels.size();i++)
                                if(channels.get(i).equals(channelName))
                                    checkIfChannelExists = true;


                            try {
                                File myObj = new File(login);
                                Scanner myReader = new Scanner(myObj);
                                while (myReader.hasNextLine()) {
                                    String data = myReader.nextLine();
                                    if(data.equals(channelName))
                                        checkIfAlrdySub = true;
                                }
                                myReader.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("Could not open the channels file");
                                e.printStackTrace();
                            }
                            if(checkIfChannelExists!=true)
                                out.println(new ErrorResponse("This channel doesnt exist"));
                            else if (checkIfAlrdySub==true)
                            {
                                out.println(new ErrorResponse("You are already sub to this channel"));
                            }
                            else
                            {
                                try {
                                    BufferedWriter myWriter = new BufferedWriter(new FileWriter(login, true));
                                    myWriter.write(channelName+"\n");
                                    myWriter.close();
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }

                                out.println(new SuccessResponse());
                            }
                        }

                        continue;
                    }

                    // unsub request? Must be logged in
                    if (login != null &&
                            (req = UnsubscribeRequest.fromJSON(json)) != null) {
                        String channelName = ((UnsubscribeRequest)req).getChannelName();
                        synchronized (ClientHandler.class)
                        {
                            List<String> subs = new ArrayList<String>();
                            boolean checkIfChannelExists = false;
                            boolean checkIfSub= false;

                            for(int i=0; i<channels.size();i++)
                            {
                                String str = channels.get(i);
                                if(channelName.equals(str))
                                    checkIfChannelExists = true;
                            }


                            if(checkIfChannelExists)
                            {
                                try {
                                    File myObj = new File(login);
                                    Scanner myReader = new Scanner(myObj);
                                    while (myReader.hasNextLine()) {
                                        String data = myReader.nextLine();
                                        if(data.equals(channelName))
                                            checkIfSub = true;
                                        else
                                            subs.add(data);
                                    }
                                    myReader.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Could not open the channels file");
                                    e.printStackTrace();
                                }
                            }

                            if(checkIfChannelExists==true && checkIfSub==true)
                            {
                                try
                                {
                                    FileWriter myWriter = new FileWriter(login);
                                    for(int i=0;i<subs.size();i++)
                                        myWriter.write(subs.get(i)+"\n");
                                    myWriter.close();
                                    out.println(new SuccessResponse());
                                }
                                catch (IOException e) {
                                    System.out.println("exception occurred" + e);
                                }
                            }
                            else if(checkIfChannelExists==false)
                                out.println(new ErrorResponse("The channel name is invalid"));
                            else
                                out.println(new ErrorResponse("You are not sub to this channel"));
                            continue;
                        }
                    }

                    // log request
                    if (login==null && (req = LogRequest.fromJSON(json)) != null) {
                        String ID = ((LogRequest)req).getUserIdentity();
                        String password = ((LogRequest)req).getPassword();
                        synchronized (ClientHandler.class)
                        {
                            File file = new File("logs.txt");
                            if(file.exists())
                            {
                                int count =0;
                                int numberOfLogs;
                                try {
                                    Scanner myReader = new Scanner(file);
                                    while (myReader.hasNextLine()) {
                                        count++;
                                        String data = myReader.nextLine();
                                    }
                                    myReader.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Could not open the channels file");
                                    e.printStackTrace();
                                }
                                boolean check = false;
                                numberOfLogs = count/2;
                                boolean idCheck = false;
                                boolean passwordCheck = false;
                                int logRead = 0;
                                int count2=0;
                                try {
                                    Scanner myReader = new Scanner(file);
                                    while (myReader.hasNextLine()) {
                                        count2++;
                                        String data = myReader.nextLine();
                                        if(count2 == 1 + logRead*2 && data.equals(ID))
                                            idCheck = true;
                                        if(count2 == 2 + logRead*2 && data.equals(ID))
                                            passwordCheck = true;
                                        if(count2%2==0)
                                        {
                                            logRead++;
                                            if(idCheck==false && passwordCheck == true)
                                                passwordCheck = false;
                                            if(idCheck==true && passwordCheck ==false)
                                                idCheck=false;
                                        }
                                        if(idCheck==true && passwordCheck==true)
                                        {
                                            check = true;
                                            break;
                                        }
                                    }
                                    myReader.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Could not open the channels file");
                                    e.printStackTrace();
                                }
                                if(check==true)
                                {
                                    login = ID;
                                    out.println(new SuccessResponse());
                                    continue;
                                }
                                else
                                {
                                    out.println(new ErrorResponse("ID or password not found"));
                                    continue;
                                }
                            }
                            else
                            {
                                out.println(new ErrorResponse("Create an account"));
                                continue;
                            }
                        }
                    }

                    // createaccount request?
                    if(login==null && (req = createAccountRequest.fromJSON(json)) != null) {
                        String ID = ((createAccountRequest) req).getUserIdentity();
                        String password = ((createAccountRequest) req).getPassword();
                        synchronized (ClientHandler.class)
                        {
                            File file = new File("logs.txt");
                            if(!file.exists())
                            {
                                file.createNewFile();
                                FileWriter myWriter = new FileWriter("logs.txt");
                                myWriter.write(ID+"\n");
                                myWriter.write(password+"\n");
                                myWriter.close();
                                login = ID;
                                try {
                                    File myObj = new File(login);
                                    myObj.createNewFile();
                                    out.println(new SuccessResponse());
                                    continue;
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                }
                            }
                            else
                            {
                                int count =0;
                                int numberOfLogs;
                                try {
                                    Scanner myReader = new Scanner(file);
                                    while (myReader.hasNextLine()) {
                                        count++;
                                        String data = myReader.nextLine();
                                    }
                                    myReader.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Could not open the channels file");
                                    e.printStackTrace();
                                }
                                boolean exists=false;
                                numberOfLogs = count/2;
                                int count2=0;
                                int logRead = 0;
                                try {
                                    Scanner myReader = new Scanner(file);
                                    while (myReader.hasNextLine()) {
                                        count2++;
                                        String data = myReader.nextLine();
                                        if(count2 == 1 + logRead*1 && data.equals(ID))
                                            exists = true;
                                        if(count%2==0)
                                            logRead++;
                                    }
                                    myReader.close();
                                } catch (FileNotFoundException e) {
                                    System.out.println("Could not open the channels file");
                                    e.printStackTrace();
                                }
                                if(exists==true)
                                {
                                    out.println(new ErrorResponse("This userID is not avaliable"));
                                    continue;
                                }
                                else
                                {
                                    try {
                                        BufferedWriter myWriter = new BufferedWriter(new FileWriter("logs.txt", true));
                                        myWriter.write(ID+"\n");
                                        myWriter.write(password+"\n");
                                        myWriter.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        File myObj = new File(ID);
                                        myObj.createNewFile();
                                    } catch (IOException e) {
                                        System.out.println("An error occurred.");
                                    }
                                    login = ID;
                                    out.println(new SuccessResponse());
                                    continue;
                                }
                            }
                        }
                    }

                    // quit request? Must be logged in; no response
                    if (login != null && QuitRequest.fromJSON(json) != null) {
                        in.close();
                        out.close();
                        return;
                    }

                    // error response acknowledging illegal request
                    out.println(new ErrorResponse("ILLEGAL REQUEST"));
                }
            } catch (IOException e) {
                System.out.println("Exception while connected");
                System.out.println(e.getMessage());
            }
        }
    }


    public static void main(String[] args) {


        Server server = new Server();
        int portNumber = 12345;
        server.retrieveChannels();
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Exception listening for connection on port " +
                    portNumber);
            System.out.println(e.getMessage());
        }
    }
}