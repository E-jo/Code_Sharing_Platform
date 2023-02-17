package platform.Controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import platform.Models.CodeFragment;
import platform.Services.CodeFragmentService;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CodeShareController {
    private CodeFragment currentFragment = new CodeFragment();

    @Autowired
    CodeFragmentService codeFragmentService;

    public String getCodeFragment() {
        return "public static void main(String[] args) {\n" +
                "    SpringApplication.run(CodeSharingPlatform.class, args);\n}";
    }

    public String getCodeFragmentJson() {
        return "{\n\"code\": \"" + getCodeFragment() + "\"\n}";
    }

    public String getCodeFragmentWeb(CodeFragment codeFragment) {
        String result = "<html>\n" +
                "<head>\n" +
                "    <title>Code</title>\n" +
                "    <link rel=\"stylesheet\"\n" +
                "       href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <pre id=\"code_snippet\">\n" +
                "<code>\n" +
                codeFragment.getCode() +
                "</code>\n" +
                "</pre>\n" +
                "<span id=\"load_date\">" +
                codeFragment.getDate().toString() +
                "</span>\n";
        if (codeFragment.getTime() > 0) {
            result += "<span id=\"time_restriction\">" +
                    codeFragment.getTime() +
                    "</span>\n";
        }
        if (codeFragment.getViews() > 0) {
            result += "<span id=\"views_restriction\">" +
                    codeFragment.getViews() +
                    "</span>\n";
        }

        return result;
    }

    public String getLatest() {
        String result = "<html>\n" +
                "<head>\n" +
                "    <title>Latest</title>\n" +
                "    <link rel=\"stylesheet\"\n" +
                "       href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>\n" +
                "</head>\n" +
                "<body>\n";

        Page<CodeFragment> results = codeFragmentService.findLatest();
        for (CodeFragment r : results) {
            System.out.println(r.getCode() + "\n" + r.getDate() + "\nviews:" + r.getViews()
                                + "\ntime:" + r.getTime());
        }
        for (CodeFragment c : results) {
            result += "    <pre id=\"code_snippet\">\n" +
                    "<code>\n" +
                    c.getCode() +
                    "</code>\n" +
                    "</pre>\n" +
                    "<span id=\"load_date\">" +
                    c.getDate().toString() +
                    "</span>\n";

        }
        result += "</body>\n" +
                "</html>";
        return result;
    }

    public String getNewCodeForm() {
        return "<html>\n" +
                "<head>\n" +
                "    <title>Create</title>\n" +
                "<script>" +
                "function send() {\n" +
                "let object = {\n" +
                "        \"code\": document.getElementById(\"code_snippet\").value,\n" +
                "        \"time\": document.getElementById(\"time_restriction\").value,\n" +
                "        \"views\": document.getElementById(\"views_restriction\").value\n" +
                "    };\n" +
                "\n" +
                "    let json = JSON.stringify(object);" +
                "    \n" +
                "    let xhr = new XMLHttpRequest();\n" +
                "    console.log(json);\n" +
                "    xhr.open(\"POST\", '/api/code/new', false);\n" +
                "    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');\n" +
                "    xhr.send(json);\n" +
                "    \n" +
                "    if (xhr.status == 200) {\n" +
                "      alert(\"Success!\");\n" +
                "    }\n" +
                "}\n" +
                "</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<input id=\"time_restriction\" type=\"text\"/>\n" +
                "<input id=\"views_restriction\" type=\"text\"/>\n" +
                "<textarea id=\"code_snippet\"></textarea><br>" +
                "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">" +
                "Submit</button>" +
                "</body>\n" +
                "</html>";
    }

    /*
    @GetMapping("/code")
    public ResponseEntity<?> getCodeWeb() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "text/html");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(getCodeFragmentWeb());
    }

    @GetMapping(value = "/api/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCodeApi() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "application/json");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(currentFragment);
    }

     */

    @GetMapping("/code/latest")
    public ResponseEntity<?> getLatestWeb() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "text/html");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(getLatest());
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<?> getCodeWeb(@PathVariable String id) {
        System.out.println("Web request received");
        Optional<CodeFragment> result = codeFragmentService.findById(id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CodeFragment resultCode = result.get();

        LocalDateTime testExp = resultCode.getExpirationDate();
        LocalDateTime testNow = LocalDateTime.now();
        String resultString = "";
        boolean markForDelete = false;
        boolean valid = false;
        if (resultCode.getViews() <= 0) {
            System.out.println("Non-View-Restricted\n" + "Views: " + resultCode.getViews());
            if (resultCode.getTime() <= 0) {
                resultCode.setTime(0L);
                System.out.println("Non-Time-Restricted\n" + "Time: " + resultCode.getTime());
                resultString = getCodeFragmentWeb(resultCode);
                valid = true;
            } else if (testNow.isBefore(testExp)) {
                long diff = ChronoUnit.SECONDS.between(testNow, testExp);
                resultCode.setTime(diff > 0 ? diff : 0);
                codeFragmentService.save(result.get());
                resultString = getCodeFragmentWeb(resultCode);
                System.out.println("Within time restriction\n" + "Time: " + resultCode.getTime());
                valid = true;
            } else {
                System.out.println("Deleted: time expired\n" + "Time: " + resultCode.getTime());
                markForDelete = true;
            }
        } else {
            resultCode.setTime(ChronoUnit.SECONDS.between(LocalDateTime.now(), resultCode.getExpirationDate()));

            System.out.println("View-Restricted\n" + "Views: " + resultCode.getViews());
            System.out.println("Time: " + resultCode.getTime());
            resultCode.setViews(result.get().getViews() - 1);
            resultString = getCodeFragmentWeb(resultCode);
            codeFragmentService.save(resultCode);
            valid = true;
            if (resultCode.getViews() == 0) {
                resultString += "<span id=\"views_restriction\">" +
                        resultCode.getViews() +
                        "</span>\n";
                markForDelete = true;
            }
        }
        resultString += "</body>\n" +
                      "</html>";

        if (markForDelete) {
            codeFragmentService.deleteById(resultCode.getId());
        }
        if (valid) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type",
                    "text/html");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(resultString);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/api/code/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCodeApi(@PathVariable String id) {
        System.out.println("API request received");
        Optional<CodeFragment> result = codeFragmentService.findById(id);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CodeFragment resultCode = result.get();

        LocalDateTime testExp = resultCode.getExpirationDate();
        LocalDateTime testNow = LocalDateTime.now();
        boolean markForDelete = false;
        boolean valid = false;
        if (result.get().getViews() <= 0) {
            System.out.println("Non-View-Restricted\n" + "Views: " + resultCode.getViews());
        } else {
            System.out.println("View-Restricted\\n\" + \"Views: " +resultCode.getViews());
            resultCode.setViews(resultCode.getViews() - 1);
            codeFragmentService.save(resultCode);
            if (result.get().getViews() == 0) {
                markForDelete = true;
            }

        }
        if (result.get().getTime() <= 0) {
            resultCode.setTime(0L);
            System.out.println("Non-Time-Restricted\n" + "Time: " + resultCode.getTime());
            valid = true;
        } else if (testNow.isBefore(testExp)) {
            long diff = ChronoUnit.SECONDS.between(testNow, testExp);
            resultCode.setTime(diff > 0 ? diff : 0);
            System.out.println("Within time restriction\n" + "Time: " + resultCode.getTime());
            valid = true;
        } else {
            System.out.println("Deleted: time expired\n" + "Time: " + resultCode.getTime());
            markForDelete = true;
        }

        if (markForDelete) {
            System.out.println("Deleted");
            codeFragmentService.deleteById(resultCode.getId());
        }
        if (valid) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type",
                    "application/json");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(resultCode);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/api/code/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLatestApi() {
        Page<CodeFragment> result = codeFragmentService.findLatest();
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (CodeFragment r : result) {
            System.out.println(r.getCode() + "\n" + r.getDate() + "\nviews:" + r.getViews()
                    + "\ntime:" + r.getTime());
        }
        List<CodeFragment> resultList = new ArrayList<>();
        for (CodeFragment c : result) {
            resultList.add(c);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "application/json");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(resultList);
    }

    @PostMapping(value = "/api/code/new")
    public ResponseEntity<?> createNewCodeFragment(@RequestBody CodeFragment inputCode) {
            //currentFragment.setCode(input.substring(9, input.length() - 2));

        inputCode.setId(UUID.randomUUID().toString());
        inputCode.setDate(LocalDateTime.now());
        inputCode.setExpirationDate(inputCode.getDate().plusSeconds(inputCode.getTime()));

        codeFragmentService.save(inputCode);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "application/json");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("{\n \"id\" : \"" + inputCode.getId() + "\" }");
    }

    @GetMapping(value = "/code/new")
    public ResponseEntity<?> createNewCodeWeb() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "text/html");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(getNewCodeForm());
    }

    @DeleteMapping("/api/deleteAll")
    public ResponseEntity<?> deleteAll() {
        codeFragmentService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
