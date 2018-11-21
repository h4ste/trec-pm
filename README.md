## Medical Information Retrieval System developed for the 2017 Text REtrieval Conference (TREC) Precision Medicine Track (TREC-PM)

:construction: Documentation under construction! :construction:

## Indexing the collections

### Indexing MEDLINE
Two MEDLINE indexes are supported: a basic index which stores documents in the inverted index (i.e., as Lucene stored fields), and a Lazy index which stores JAXB-parsed articles in a separate forward index (i.e., Lucene docvalues). The Lazy JAXB-based index is preferred.

To create a lazy JAXB-based index, issue the following command:
```shell
$ JaxbMedlineIndexer INDEX_DIR [--replace] INPUT_DIR1, INPUT_DIR2, ....
```
   
This will create a Lucene index at `INDEX_DIR`.

### Indexing Cancer Abstracts
Execute:
```shell
ExtraCancerAbstratIndexer INDEX_DIR INPUT_DIR1, INPUT_DIR2, ...
```
    
### Indexing ClinicalTrials.gov
Clinical trials can be indexed with and without eligibility criteria parsing. To enable parsing eligibility critieria (i.e., detection of inclusion and exclusion criteria), it is necessary to install (1) GATE for sentence splitting, (2) GENIA for tokenization, and (3) LingScope for negation detection.

#### Enabling Eligibility Criteria Parsing
##### Installing GATE
```shell
mkdir -p tools
pushd tools
wget 'https://downloads.sourceforge.net/project/gate/gate/8.0/gate-8.0-build4825-BIN.zip'
unzip gate-8.0-build4825-BIN.zip && rm gate-8.0-build4825-BIN.zip
export GATE_HOME=$PWD/gate-8.0-build4825-BIN/
popd
 ```
 
 #### Installing GENIA
 ```shell
mkdir -p tools
wget -qO- http://www.nactem.ac.uk/tsujii/GENIA/tagger/geniatagger-3.0.2.tar.gz | tar -C tools -xvzf -
pushd tools/geniatagger-3.0.2
make
popd
```

#### Installing LingScope
```shell
mkdir -p tools/lingscope
pushd tools/lingscope
wget -q 'https://downloads.sourceforge.net/project/lingscope/negation_models.zip' 'https://downloads.sourceforge.net/project/lingscope/hedge_models.zip'
unzip negation_models.zip && rm negation_models.zip
unzip hedge_models.zip && rm hedge_models.zip
popd
mkdir -p lib
pushd lib
wget 'https://downloads.sourceforge.net/project/lingscope/lingscope_v3/dist/lingscope.jar' 'https://downloads.sourceforge.net/project/lingscope/lingscope_v3/dist/lib/abner.jar'
popd
```

Execute:
```shell
ClinicalTrialIndexerCli [-n|--negate] [-D|--delete] INDEX_DIR  TRIAL_INPUT_DIR1, TRIAL_INPUT_DIR2, ...
```
    

## Running the system
Execute:
```shell
Driver [-m|--model] [-t|--runtag] TOPICS_FILE OUTPUT_DIR
````   
where
- the `model` option is one of
  - `JOINT`: , 
  - `ASPECT_FUSION`: , 
  - `SIMILARITY_FUSION`: , 
  - `FUSION_FUSION`: ,
  - `SIMPLE`: ,
  - `STUPID`: ,
- and the `runtag` option is the runtag to be used in TREC-style submission files (e.g., UTDHLTRI)

The system will produce TREC-style run/submission files in `OUTPUT_DIR`:
* `medline_submission.txt` includes the results of the system for Task A (i.e., operating on MEDLINE and conference proceedings)
* `clinical_trial_submission.txt` includes the results of the system for Task B (i.e., operating on ClinicalTrials.gov)
 
### Merging runs
The different retrieval models trade recall for precision. In order to return up to 1,000 articles/trials per topic, we append the retrieved documents from multiple runs.

```shell
SubmissionMerger [--runTag] [-L|--limit"] OUTPUT_FILE RUN_1, RUN_2, ...
```

This will produce a single output file where-in the results for each topic will be the results retrieved by `RUN_1`, followed by those retrieved by `RUN_2`, then `RUN_3`, etc.

## Visualizing Search Results
Static HTML pages visualizing (1) retrieved articles, (2) topic analysis, and (3) generated Lucene queries will be generated when the system executes. These files may be found in `OUTPUT_DIR`:
* `medline_results.html` includes the results of the system for Task A (i.e., operating on MEDLINE and conference proceedings)
* `clinical_trial_results.html` includes the results of the system for Task B (i.e., operating on ClinicalTrials.gov)

