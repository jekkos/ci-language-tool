This is a java command line utility that converts a set of existing CodeIgniter language files into a number of CSV files.
The CSV files will be structured such that all translations are easily managable. Format is as follows

   lang    |     lang1    |    lang2
----------------------------------------
label name | translation1 | translation2

Starting from the CSV files, language files can be automatically generated using the generate_languages.php script

To compile and run, issues the following command

     git clone https://github.com/jekkos/ci-language-tool.git
     cd ci-language-tool
     mvn install
     cd target
     java -jar ci-language-tool-0.1-SNAPSHOT.jar <path/to/ci-language-folder>

If you are done editing the CSV files and want to generate the language files out of it, execute following command

    cd ci-language-tool
    php generate_languages.php
