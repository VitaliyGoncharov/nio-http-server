# nio-http-server
An http server made with NIO

### How to start
```Java
Server server = new Server("localhost", 80);
server.start();
```

OR you can create Server object without arguments, it will use default parameters
```Java
Server server = new Server();
server.start();
```

Default parameters:
```Java
private final static String HOST = "localhost";
private final static int PORT = 80;
public final static String SERVER_NAME = "NioHttpServer v0.1";
public final static int MAX_POST_SIZE = 21_000_000; // 21 MB
```

Also next constructors are available:
```Java
public Server();
public Server(int port);
public Server(String host, int port);
public Server(String host, int port, int maxPostSize, String serverName);
```

### How to make and add handler

All your handlers must implement `com.vitgon.httpserver.request.RequestHandler`.

Simple handler that returns string:
```Java
import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;

public class HomeHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		response.setResponseBody("Hello world!");
	}
}
```

Then add this HomeHanlder to server:
```Java
import com.vitgon.httpserver.enums.HttpMethod;

public class Starter {
	public static void main(String[] args) {
		Server server = new Server("localhost", 80);
		server.addHandler("/", HttpMethod.GET, new HomeHandler());
		server.start();
	}
}
```

You can open your browser on http://localhost:80 and you will see "Hello world!" message.

### Using thymeleaf

In your handler you can set model attributes and return thymeleaf page.
`index` is a name of html file that exists on path `src/main/resources/templates/`
Thymeleaf will search html files only in `templates` folder, but you can customize it.
```Java
public class HomeHandler implements RequestHandler {

  @Override
  public void handle(Request request, Response response) {
    String name = "Guest";
    response.setModelAttribute("name", name);
    response.setResponsePage("index");
  }
}
```

### Upload file in Handler
All uploaded files are stored in parts `com.vitgon.httpserver.data.Part`.
You can get part by its name `request.getPart(String name)` or get list of parts `request.getParts()`.

```Java
import java.io.IOException;

import com.vitgon.httpserver.data.Part;
import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;
import com.vitgon.httpserver.util.FileUtil;

public class UploadFileHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		Part part = request.getPart("rabbit");
		if (part != null) {
			try {
				FileUtil.saveToFile("src/main/resources/uploads/" + part.getFilename(), part.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setResponseBody("<b>File " + part.getFilename() + "was successfully saved!");
		response.sendRedirect("/upload.html", 4);
	}	
}
```
