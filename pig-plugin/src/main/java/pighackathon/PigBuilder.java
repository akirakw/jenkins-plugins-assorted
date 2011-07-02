package pighackathon;

import hudson.Extension;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.CommandInterpreter;
import hudson.util.FormValidation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Builder for launching Pig Latin.
 *
 * @author Akira Kawaguchi
 */
public class PigBuilder extends CommandInterpreter {

    public PigBuilder(String command) {
        super(command);
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException {

        super.perform(build, launcher, listener);

        listener.getLogger().println("この豚野郎!");
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public String[] buildCommandLine(FilePath script) {
        return new String[] { getDescriptor().getShellOrDefault(), "-xe", script.getRemote() };
    }

    @Override
    protected String getContents() {
        File dir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = null;
        try {
            tempFile = File.createTempFile("pigtemp", ".pig", dir);
            tempFile.deleteOnExit();

            Writer w = new FileWriter(tempFile);
            w.write(command);
            w.close();

        } catch (IOException e) {
            // ここで例外が発生した場合は握りつぶす。
        }
        return "pig -x local -f " + tempFile.toString();
    }

    @Override
    protected String getFileExtension() {
        return ".sh";
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private String shell;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public void setShell(String shell) {
            save();
        }

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject data) {
            return new PigBuilder(data.getString("command"));
        }

        @Override
        public String getDisplayName() {
            return "豚";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject data) {
            setShell(req.getParameter("shell"));
            return true;
        }

        public String getShellOrDefault() {
            return Functions.isWindows() ? "sh" : "/bin/sh";
        }

        public FormValidation doCheck(@QueryParameter String value) {
            return FormValidation.validateExecutable(value);
        }

    }

}
