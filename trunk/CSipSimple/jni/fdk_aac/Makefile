# This makefile aim is only for now to address source checkout and patch
fdk_tag := jb-release
fdk_url := https://android.googlesource.com/platform/external/aac
patches_files := $(wildcard patches/*.diff)

all :
	# For now we do nothing to build - it's made by android_toolchain
	
init : sources
	# Init done


patch : .patched_sources
	@# Patch done

.patched_sources : $(patches_files) sources
	@quilt push -a && \
	touch .patched_sources
	
unpatch :
	@if [ -f .patched_sources ]; then quilt pop -af; rm .patched_sources; fi;

sources :
	#@git clone $(fdk_url) sources; 
	cp -rf /home/bang/third/aac . ; \
	mv aac sources; \
	cd sources; \
	git checkout $(fdk_tag)

update : sources
	cd sources; \
	git checkout $(fdk_tag)
