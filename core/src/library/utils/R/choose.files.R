#  File src/library/utils/R/windows/choose.files.R
#  Part of the R package, http://www.R-project.org
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  A copy of the GNU General Public License is available at
#  http://www.r-project.org/Licenses/

Filters <-
structure(c("R or S files (*.R,*.q,*.ssc,*.S)",
            "Enhanced metafiles (*.emf)",
			"Postscript files (*.ps)",
			"PDF files (*.pdf)",
			"Png files (*.png)",
			"Windows bitmap files (*.bmp)",
			"Jpeg files (*.jpeg,*.jpg)",
			"Text files (*.txt)",
			"R images (*.RData,*.rda)",
			"Zip files (*.zip)",
			"All files (*.*)",
			"*.R;*.q;*.ssc;*.S", "*.emf",
			"*.ps", "*.pdf", "*.png",
			"*.bmp", "*.jpeg;*.jpg", "*.txt",
			"*.RData;*.rda", "*.zip", "*.*"),
       .Dim = c(11, 2),
       .Dimnames = list(c("R", "emf",
       					"ps","pdf", "png",
       					"bmp", "jpeg", "txt",
       					"RData", "zip", "All"), NULL))

choose.files <- function(default = '', caption = 'Select files', multi = TRUE,
                         filters=Filters, index = nrow(Filters) ) {
	.Internal(chooseFiles(default, caption, multi, filters, index))
}

choose.dir <- function(default = '', caption = 'Select folder')
    .Internal(chooseDir(default, caption))
