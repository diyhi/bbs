package cms.web.taglib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTag;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag;
import org.springframework.web.servlet.tags.form.FormTag;
import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * Form tag for displaying errors for a particular field or object.
 *
 * <p>This tag supports three main usage patterns:
 *
 * <ol>
 *	<li>Field only - set '<code>path</code>' to the field name (or path)</li>
 *	<li>Object errors only - omit '<code>path</code>'</li>
 *	<li>All errors - set '<code>path</code>' to '<code>*</code>'</li>
 * </ol>
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 */
public class FormErrorsTag extends AbstractHtmlElementBodyTag implements BodyTag {

	/**
	 * The key under which this tag exposes error messages in
	 * the {@link PageContext#PAGE_SCOPE page context scope}.
	 */
	public static final String MESSAGES_ATTRIBUTE = "messages";

	/**
	 * The HTML '<code>span</code>' tag.
	 */
	public static final String SPAN_TAG = "span";


	private String element = SPAN_TAG;

	private String delimiter = "<br/>";

	/**
	 * Stores any value that existed in the 'errors messages' before the tag was started.
	 */
	private Object oldMessages;

	private boolean errorMessagesWereExposed;


	/**
	 * Set the HTML element must be used to render the error messages.
	 * <p>Defaults to an HTML '<code>&lt;span/&gt;</code>' tag.
	 */
	public void setElement(String element) {
		Assert.hasText(element, "'element' cannot be null or blank");
		this.element = element;
	}

	/**
	 * Get the HTML element must be used to render the error messages.
	 */
	public String getElement() {
		return this.element;
	}

	/**
	 * Set the delimiter to be used between error messages.
	 * <p>Defaults to an HTML '<code>&lt;br/&gt;</code>' tag.
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Return the delimiter to be used between error messages.
	 */
	public String getDelimiter() {
		return this.delimiter;
	}


	/**
	 * Get the value for the HTML '<code>id</code>' attribute.
	 * <p>Appends '<code>.errors</code>' to the value returned by {@link #getPropertyPath()}
	 * or to the model attribute name if the <code>&lt;form:errors/&gt;</code> tag's
	 * '<code>path</code>' attribute has been omitted.
	 * @return the value for the HTML '<code>id</code>' attribute
	 * @see #getPropertyPath()
	 */
	@Override
	protected String autogenerateId() throws JspException {
		String path = getPropertyPath();
		if ("".equals(path) || "*".equals(path)) {
			path = (String) this.pageContext.getAttribute(
					FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
		}
		return StringUtils.deleteAny(path, "[]") + ".errors";
	}

	/**
	 * Get the value for the HTML '<code>name</code>' attribute.
	 * <p>Simply returns <code>null</code> because the '<code>name</code>' attribute
	 * is not a validate attribute for the '<code>span</code>' element.
	 */
	@Override
	protected String getName() throws JspException {
		return null;
	}

	/**
	 * Should rendering of this tag proceed at all?
	 * <p>Only renders output when there are errors for the configured {@link #setPath path}.
	 * @return <code>true</code> only when there are errors for the configured {@link #setPath path}
	 */
	@Override
	protected boolean shouldRender() throws JspException {
		try {
			return getBindStatus().isError();
		}
		catch (IllegalStateException ex) {
			// Neither BindingResult nor target object available.
			return false;
		}
	}

	@Override
	protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag(getElement());
		writeDefaultAttributes(tagWriter);
	//	String delimiter = ObjectUtils.getDisplayString(evaluate("delimiter", getDelimiter()));
		String[] errorMessages = getBindStatus().getErrorMessages();	
		for (int i = errorMessages.length-1; i < errorMessages.length; i++) {
			String errorMessage = errorMessages[i];
		//	if (i > 0) {
			//	tagWriter.appendValue(delimiter);//换行
		//	}
			
			tagWriter.appendValue(getDisplayString(errorMessage));
		}
		tagWriter.endTag();
	}

	/**
	 * Exposes any bind status error messages under {@link #MESSAGES_ATTRIBUTE this key}
	 * in the {@link PageContext#PAGE_SCOPE}.
	 * <p>Only called if {@link #shouldRender()} returns <code>true</code>.
	 * @see #removeAttributes()
	 */
	@Override
	protected void exposeAttributes() throws JspException {
		List<String> errorMessages = new ArrayList<String>();
		errorMessages.addAll(Arrays.asList(getBindStatus().getErrorMessages()));
		this.oldMessages = this.pageContext.getAttribute(MESSAGES_ATTRIBUTE, PageContext.PAGE_SCOPE);
		this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, errorMessages, PageContext.PAGE_SCOPE);
		this.errorMessagesWereExposed = true;
	}

	/**
	 * Removes any bind status error messages that were previously stored under
	 * {@link #MESSAGES_ATTRIBUTE this key} in the {@link PageContext#PAGE_SCOPE}.
	 * @see #exposeAttributes()
	 */
	@Override
	protected void removeAttributes() {
		if (this.errorMessagesWereExposed) {
			if (this.oldMessages != null) {
				this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, this.oldMessages, PageContext.PAGE_SCOPE);
				this.oldMessages = null;
			}
			else {
				this.pageContext.removeAttribute(MESSAGES_ATTRIBUTE, PageContext.PAGE_SCOPE);
			}
		}
	}

}

