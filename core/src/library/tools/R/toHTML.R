toHTML <- function(x, ...) UseMethod("toHTML")


HTMLheader <-
function(title="R", logo=TRUE,
         up=NULL,
         top=file.path(Rhome, "doc/html/index.html"),
         Rhome="",
         css = file.path(Rhome, "doc/html/R.css"),
         headerTitle = paste("R:", title),
         outputEncoding = "UTF-8")
{
    result <- c('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">',
        paste('<html><head><title>', headerTitle, '</title>', sep=''),
        paste('<meta http-equiv="Content-Type" content="text/html; charset=',
              mime_canonical_encoding(outputEncoding), '">', sep=''),
        paste('<link rel="stylesheet" type="text/css" href="', css, '">', sep=''),
        '</head><body>',
	paste('<h1>', title))
    if (logo)
    	result <- c(result, paste('<img class="toplogo" src="',
	      file.path(Rhome, 'doc/html/logo.jpg'), '" alt="[R logo]">', sep=''))
    result <- c(result, '</h1>', '<hr>')
    if (!is.null(up) || !is.null(top)) {
    	result <- c(result, '<div align="center">')
    	if (!is.null(up))
    	    result <- c(result,
    	        paste('<a href="', up, '"><img src="',
    	              file.path(Rhome, 'doc/html/left.jpg'),
    	              '" alt="[Up]" width="30" height="30" border="0"></a>',
    	              sep=''))
    	if (!is.null(top))
    	    result <- c(result,
    	    	paste('<a href="', top, '"><img src="',
    	    	      file.path(Rhome, 'doc/html/up.jpg'),
    	    	      '" alt="[Top]" width="30" height="30" border="0"></a>',
    	    	      sep=''))
    	result <- c(result, '</div>')
    }
    result
}

toHTML.packageIQR <-
function(x, ...)
{
    db <- x$results

    # Re-encode as utf-8
    x$title <- iconv(x$title, to="UTF-8")
    x$footer <- iconv(x$footer, to="UTF-8")
    db <- iconv(db, to="UTF-8")

    ## Split according to Package.
    out <- if(nrow(db) == 0L)
         NULL
    else
        lapply(split(1 : nrow(db), db[, "Package"]),
               function(ind) db[ind, c("Item", "Title"),
                                drop = FALSE])

    result <- HTMLheader(...)

    for(pkg in names(out)) {
        result <- c(result,
                    paste('<h2>', htmlify(x$title), ' in package &lsquo;',
                                  htmlify(pkg), '&rsquo;</h2>', sep = ''),
                    '<table cols="2" width="100%">',
                    paste('<tr>\n',
                          ' <td align="left" valign="top" width="10%">\n',
                          htmlify(out[[pkg]][, "Item"]),
                          '\n </td>\n <td align="left" valign="top" width="90%">\n',
                          htmlify(out[[pkg]][, "Title"]),
                          '\n </td>\n</tr>\n', sep = ''),
                    '</table>')
    }
    if(!is.null(x$footer))
    	result <- c(result, '<p>',
    	                    htmlify(x$footer),
    	                    '</p>')
    result <- c(result, '</body></html>')
    result
}

toHTML.news_db <-
function(x, ...)
{
    ## local version
    htmlify2 <- function(x) {
        x <- psub("<([[:alnum:]._]+)>", "@VAR@\\1@EVAR@", x)
        x <- fsub("&", "&amp;", x)
        x <- fsub("---", "&mdash;", x)
        ## usually a flag like --timing
        ## x <- fsub("--", "&ndash;", x)
        x <- fsub("``", "&ldquo;", x)
        x <- fsub("''", "&rdquo;", x)
        x <- psub("`([^']+)'", "&lsquo;\\1&rsquo;", x)
        x <- fsub("`", "'", x)
        x <- fsub("<", "&lt;", x)
        x <- fsub(">", "&gt;", x)
        x <- fsub("@VAR@", "<var>", x)
        x <- fsub("@EVAR@", "</var>", x)
        x
    }

    ## For now, only do something if the NEWS file could be read without
    ## problems, see utils:::print.news_db():
    if(is.null(bad <- attr(x, "bad"))
       || (length(bad) != NROW(x))
       || any(bad))
        return(character())

    print_items <- function(x)
        c("<ul>", sprintf("<li>%s</li>", htmlify2(x)), "</ul>")

    x$Text <- iconv(x$Text, to = "UTF-8")

    vchunks <- split(x, x$Version)
    vchunks <-
        vchunks[order(as.numeric_version(sub(" *patched", ".1",
                                             names(vchunks))),
                      decreasing = TRUE)]
    vheaders <- sprintf("<h2>Changes in version %s</h2>",
                        names(vchunks))
    c(HTMLheader(...),
      unlist(lapply(seq_along(vchunks),
                    function(i) {
                        vchunk <- vchunks[[i]]
                        if(all(!is.na(category <- vchunk$Category)
                               & nzchar(category))) {
                            ## need to preserve order of headings.
                            cchunks <- split(vchunk,
                                             factor(category, levels=unique(category)))
                            c(vheaders[i],
                              Map(function(h, t)
                                  c(h, print_items(t$Text)),
                                  sprintf("<h3>%s</h3>", names(cchunks)),
                                  cchunks))
                        } else {
                            c(vheaders[i],
                              print_items(vchunk$Text))
                        }
                    })
             ),
      "</body></html>")
}

# To support static linking, URLs should be relative.
# Argument "depth" below says how far down in the hierarchy
# we are starting from, e.g. /library/stats/html/mean.html
# is depth 3

makeVignetteTable <- function(vignettes, depth=2) {
    out <- c('<table width="100%">',
	      '<col width="22%">',
	      '<col width="2%">',
	      '<col width="50%">',
	      '<col width="8%">',
	      '<col width="8%">',
	      '<col width="8%">')
    for (i in seq_len(nrow(vignettes))) {
	topic <- file_path_sans_ext(vignettes[i, "File"])
	Title <- vignettes[i, "Title"]
	PDF   <- vignettes[i, "PDF"]
	File  <- vignettes[i, "File"]
	R     <- vignettes[i, "R"]
	pkg   <- vignettes[i, "Package"]
        root <- c(rep("../", depth), "library/", pkg, "/doc/")
	link  <- c('<a href="', root, 
		  if (nchar(PDF)) PDF else File, '">', 
		  pkg, "::", topic, '</a>')
	line <- c('<tr><td align="right" valign="top">', link, 
		    '</td>\n<td></td><td valign="top">', Title, 
		    '</td>\n<td valign="top">', 
		    if (nchar(PDF))
			c('<a href="', root, PDF,'">PDF</a>'),
		    '</td>\n<td valign="top">',
		    '<a href="', root, File,'">source</a>',
		    '</td>\n<td valign="top" nowrap>',
		    if (nchar(R))
		    	c('<a href="', root, R,'">R code</a>'),
		    '</td></tr>')      
	out <- c(out, paste(line, collapse=''))
     }
     c(out, '</table>')
}

makeDemoTable <- function(demos, depth=2) {
    out <- c('<table width="100%">',
	      '<col width="22%">',
	      '<col width="2%">',
	      '<col width="54%">',
	      '<col width="20%">')
    for (i in seq_len(nrow(demos))) {
	topic <- demos[i, "topic"]
	pkg <- demos[i, "Package"]
        root <- c(rep("../", depth), "library/", pkg, "/")	      
	Title <- demos[i, "title"]
	path <- file.path(demos[i, "LibPath"], "demo")
	files <- basename(list_files_with_type(path, "demo", full.names=FALSE))
	file <- files[topic == file_path_sans_ext(files)]
	if (length(file) == 1) { 
	    link <- c('<a href="', root, 'demo/', file, '">', 
			  pkg, "::", topic, '</a>')
	    runlink <- c(' <a href="', root, 'Demo/', topic, 
	                 '">(Run demo in console)</a>')					  	
	} else {
	    link <- c(pkg, "::", topic)
	    runlink <- ""
	}
	line <- c('<tr><td align="right" valign="top">', link, 
		    '</td>\n<td></td><td valign="top">', Title, 
		    '</td>\n<td valign="top" nowrap>', runlink, 
		    '</td></tr>')      
	out <- c(out, paste(line, collapse=''))
     }
     c(out, '</table>')
}

makeHelpTable <- function(help, depth=2) {
    out <- c('<table width="100%">',
	      '<col width="22%">',
	      '<col width="2%">',
	      '<col width="74%">')
    pkg <- help[,"Package"]
    root <- paste(paste(rep("../", depth), collapse=""),
                  "library/", pkg, "/html/", sep="")	      
    topic <- help[, "topic"]
    Title <- help[, "title"]
    name <- help[, "name"]
    links <- paste('<a href="', root, name, '.html">',
		   ifelse(nchar(pkg), paste(pkg, "::", sep=""), ""),
		   topic, '</a>', sep = "")
    lines <- paste('<tr><td align="right" valign="top">', links, 
		   '</td>\n<td></td><td valign="top">', Title, 
		   '</td></tr>', sep="")  
    c(out, lines, '</table>')
}
