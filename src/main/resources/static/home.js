/**
 * Created by Natallia on 23.05.2017.
 */
// bind the on-change event
$(document).ready(function() {
    $("#upload-file-input").on("change", uploadFile);
});

/**
 * Upload the file sending it via Ajax at the Spring Boot server.
 */
function uploadFile() {
    $.ajax({
        url: "/uploadFile",
        type: "POST",
        data: new FormData($("#upload-file-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        success: function () {
        },
        error: function () {
            // Handle upload error
            // ...
        }
    });
}
