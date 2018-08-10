package http.Responders.Handlers;

import http.Requesters.HTTPVerb;
import http.Requesters.Request;
import http.Responders.ContentType;
import http.Responders.Response;
import http.Responders.ResponseHeader;
import http.Responders.ResponseStatus;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class DirectoryHandlerTest {

    private final static String testRootPath = "src/test/resources";
    private final static String realDirPath = "/testDir/";
    private final static String falseDirPath = "/noDirectoryHere/";
    private final static HashMap<String, String> emptyHeaders = new HashMap<>();
    private final static String emptyBody = "";

    @Test
    public void handlesPathsEndingInSlash() {
        DirectoryHandler directoryHandler = new DirectoryHandler(testRootPath);
        Request request = new Request(HTTPVerb.GET, "/endsInSlash/", emptyHeaders, emptyBody);

        assertTrue(directoryHandler.isHandledPathSegment(request));
    }

    @Test
    public void rejectsPathsNotEndingInSlash() {
        DirectoryHandler directoryHandler = new DirectoryHandler(testRootPath);
        Request request = new Request(HTTPVerb.GET, "/doesNotEndInSlash", emptyHeaders, emptyBody);

        assertFalse(directoryHandler.isHandledPathSegment(request));
    }

    @Test
    public void getResponse_getDir_exists() {
        Response response = getGetResponse(HTTPVerb.GET, realDirPath);

        String expectedDirListing = "<html><head></head><body><a href=" +
                "'/testFile2.txt'>testFile2.txt</a><br></body></html>";

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertArrayEquals(ContentType.HTML.getBytesValue(), response.getHeaders().get(ResponseHeader.CONTENTTYPE));
        assertArrayEquals(expectedDirListing.getBytes(), response.getBodyContent());
    }

    @Test
    public void getResponse_getDir_doesNotExist() {
        Response response = getGetResponse(HTTPVerb.GET, falseDirPath);

        assertEquals(ResponseStatus.NOTFOUND, response.getStatus());
        assertArrayEquals(ResponseStatus.NOTFOUND.getStatusBody(), response.getBodyContent());
    }

    @Test
    public void getResponse_headDir_exists() {
        Response response = getGetResponse(HTTPVerb.HEAD, realDirPath);

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertArrayEquals("".getBytes(), response.getBodyContent());
    }

    @Test
    public void getResponse_headDir_doesNotExist() {
        Response response = getGetResponse(HTTPVerb.HEAD, falseDirPath);

        assertEquals(ResponseStatus.NOTFOUND, response.getStatus());
        assertArrayEquals("".getBytes(), response.getBodyContent());
    }

    private Response getGetResponse(HTTPVerb httpVerb, String path) {
        DirectoryHandler directoryHandler = new DirectoryHandler(testRootPath);
        Request request = new Request(httpVerb, path, emptyHeaders, emptyBody);

        return directoryHandler.getResponse(request);
    }
}