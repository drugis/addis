(define (addis-version-header version source target)
	(define (my-save-png image filename)
		(file-png-save-defaults RUN-NONINTERACTIVE image 
		(car (gimp-image-merge-visible-layers image CLIP-TO-IMAGE))
		filename filename)
	)

	(let* ((header-image (car (gimp-file-load RUN-NONINTERACTIVE source source))))
	
		(gimp-text-layer-set-text (car (gimp-image-get-active-layer header-image)) version)

		(my-save-png header-image target)
	)
)
