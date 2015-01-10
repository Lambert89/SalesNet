'use strict';

/**
 * @ngdoc function
 */
angular
		.module('webContentApp')
		.controller(
				'FileUploadCtrl',
				function($scope, FileUploader, SweetAlert) {
					var uploader = $scope.uploader = new FileUploader({
						url : 'api/admin/fileupload',
						alias : "files"
					});

					// FILTERS

					uploader.filters.push({
						name : 'customFilter',
						fn : function(item /* {File|FileLikeObject} */,
								options) {
							var filename = item.name;
							return (filename.indexOf(".pdf", filename.length
									- ".pdf".length) !== -1);
						}
					});

					// CALLBACKS

					uploader.onWhenAddingFileFailed = function(
							item /* {File|FileLikeObject} */, filter, options) {
						console.info('onWhenAddingFileFailed', item, filter,
								options);
						SweetAlert
								.swal(
										'Oops...',
										'Only PDF files are support! Please ensure you only choose file with "pdf" as extension.',
										'error');
					};
					uploader.onAfterAddingFile = function(fileItem) {
						console.info('onAfterAddingFile', fileItem);
					};
					uploader.onAfterAddingAll = function(addedFileItems) {
						console.info('onAfterAddingAll', addedFileItems);
					};
					uploader.onBeforeUploadItem = function(item) {
						console.info('onBeforeUploadItem', item);
					};
					uploader.onProgressItem = function(fileItem, progress) {
						console.info('onProgressItem', fileItem, progress);
					};
					uploader.onProgressAll = function(progress) {
						console.info('onProgressAll', progress);
					};
					uploader.onSuccessItem = function(fileItem, response,
							status, headers) {
						console.info('onSuccessItem', fileItem, response,
								status, headers);
						SweetAlert.swal('Congratulations!',
								'File Upload Success!', 'success');
					};
					uploader.onErrorItem = function(fileItem, response, status,
							headers) {
						console.info('onErrorItem', fileItem, response, status,
								headers);
						SweetAlert.swal('Oops...',
								'Something went wrong! Upload failded.',
								'error');
					};
					uploader.onCancelItem = function(fileItem, response,
							status, headers) {
						SweetAlert.swal({
							title : 'Are you sure?',
							text : 'Your will cancel the uploading file!',
							type : 'warning',
							showCancelButton : true,
							confirmButtonColor : '#DD6B55',
							confirmButtonText : 'Yes, cancell it!'
						}, function() {
							SweetAlert.swal('Cancell!');
							console.info('onCancelItem', fileItem, response,
									status, headers);
						});
					};
					uploader.onCompleteItem = function(fileItem, response,
							status, headers) {
						console.info('onCompleteItem', fileItem, response,
								status, headers);
					};
					uploader.onCompleteAll = function() {
						console.info('onCompleteAll');
					};

					console.info('uploader', uploader);
				});
