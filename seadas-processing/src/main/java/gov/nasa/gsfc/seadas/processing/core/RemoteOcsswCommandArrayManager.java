package gov.nasa.gsfc.seadas.processing.core;

import gov.nasa.gsfc.seadas.processing.general.SeadasFileUtils;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by aabduraz on 6/12/16.
 */
public class RemoteOcsswCommandArrayManager extends OcsswCommandArrayManager {

    private HashMap<String, String> iFilesOriginalLocations;
    private HashMap<String, String> oFilesOriginalLocations;

    public RemoteOcsswCommandArrayManager(ProcessorModel processorModel) {
        super(processorModel);
        iFilesOriginalLocations = new HashMap<>();
        oFilesOriginalLocations = new HashMap<>();
    }


    /**
     * this method returns a command array for execution.
     * the array is constructed using the paramList data and input/output files.
     * the command array structure is: full pathname of the program to be executed, input file name, params in the required order and finally the output file name.
     * assumption: order starts with 1
     *
     * @return
     */
    public String[] getProgramCommandArray() {

        String[] cmdArrayParam = new String[paramList.getParamArray().size()];

        Iterator itr = paramList.getParamArray().iterator();

        ParamInfo option;
        int optionOrder;
        String optionValue;
        ParamInfo.Type optionType;

        while (itr.hasNext()) {
            option = (ParamInfo) itr.next();
            optionOrder = option.getOrder();
            optionValue = option.getValue();
            optionType = option.getType();
            if ((optionType.equals(ParamInfo.Type.IFILE) || optionType.equals(ParamInfo.Type.OFILE))
                    && optionValue != null && optionValue.trim().length() > 0) {
                //replace shared folder name for remote server
                if (optionValue.indexOf(OCSSW.getOCSSWClientSharedDirName()) != 0) {
                    //save the original file location for later usage; copy file to the shared folder; change the value of "optionValue"
                    String fileName = optionValue.substring(optionValue.lastIndexOf(System.getProperty("file.separator")) + 1);
                    String dirPath = optionValue.substring(0, optionValue.lastIndexOf(System.getProperty("file.separator")));
                    //if the file is an input file, copy it to the shared folder
                    if (optionType.equals(ParamInfo.Type.IFILE)) {
                        getiFilesOriginalLocations().put(fileName, dirPath);
                        //todo: this should be revisited! Find_next_level_name copies the ifile into the shared directory.
                        //SeadasFileUtils.copyFile(optionValue, OCSSW.getOCSSWClientSharedDirName());
                    } else if (optionType.equals(ParamInfo.Type.IFILE)) {
                        getoFilesOriginalLocations().put(fileName, dirPath);
                    }
                    optionValue = OCSSW.getServerSharedDirName() + System.getProperty("file.separator") + fileName;
                } else {
                    optionValue = optionValue.replace(OCSSW.getOCSSWClientSharedDirName(), OCSSW.getServerSharedDirName());
                }

            }
            if (option.getUsedAs().equals(ParamInfo.USED_IN_COMMAND_AS_ARGUMENT)) {
                if (option.getValue() != null && option.getValue().length() > 0) {
                    cmdArrayParam[optionOrder] = optionValue;
                }
            } else if (option.getUsedAs().equals(ParamInfo.USED_IN_COMMAND_AS_OPTION) && !option.getDefaultValue().equals(option.getValue())) {
                cmdArrayParam[optionOrder] = option.getName() + "=" + optionValue;
            } else if (option.getUsedAs().equals(ParamInfo.USED_IN_COMMAND_AS_FLAG) && (option.getValue().equals("true") || option.getValue().equals("1"))) {
                if (option.getName() != null && option.getName().length() > 0) {
                    cmdArrayParam[optionOrder] = option.getName();
                }
            }
        }
        return cmdArrayParam;
    }

    public HashMap<String, String> getiFilesOriginalLocations() {
        return iFilesOriginalLocations;
    }

    public void setiFilesOriginalLocations(HashMap<String, String> iFilesOriginalLocations) {
        this.iFilesOriginalLocations = iFilesOriginalLocations;
    }

    public HashMap<String, String> getoFilesOriginalLocations() {
        return oFilesOriginalLocations;
    }

    public void setoFilesOriginalLocations(HashMap<String, String> oFilesOriginalLocations) {
        this.oFilesOriginalLocations = oFilesOriginalLocations;
    }
}
