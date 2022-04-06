import React, {Component} from 'react';

import './AutoReplyDetailsModal.css';
import '../common/Common.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

class AutoReplyDetailsModal extends Component {
  constructor(props) {
    super(props);

    var state = {}
    if (props.selectedAutoReply && props.selectedAutoReply.id) {
      state = {
        keywords: props.selectedAutoReply.keywords,
        message: props.selectedAutoReply.message,
        id: props.selectedAutoReply.id,
        title: 'Update Auto Reply',
        keywordsError: false,
        messageError: false,
        submitButtonLabel: 'Update'};
    } else {
      state = {
        keywords: '',
        message: '',
        id: 0,
        title: 'New Auto Reply',
        keywordsError: false,
        messageError: false,
        submitButtonLabel: 'Add'};
    }

    this.state = state;
  }

  addNewAutoReply() {
    const autoReply = {
      keywords: this.state.keywords,
      message: this.state.message,
      id: this.state.id,
      userId: Session.getUser().id
    }

    this.setState({keywordsError: false, messageError: false});

    if (autoReply.keywords.length === 0) {
      this.setState({keywordsError: true});
    }

    if (autoReply.message.length === 0) {
      this.setState({messageError: true});
    }

    if (autoReply.message.length) {
      CampaignsAPI.createAutoReply(autoReply,
      response => {
        if (response.status === 'OK') {
          this.props.handleClose(true);
        }
      });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      nameError: false,
      [field]: event.target.value
    });
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-group-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            error={this.state.keywordsError}
            margin="dense"
            id="name"
            label="Enter keywords separated with comma"
            type="text"
            value={this.state.keywords}
            onChange={(e) => this.onTextFieldChanged(e, "keywords")}
            fullWidth
            multiline
            rowsMax="2"
          />

          <Typography className="auto-reply-tip margin-bottom-10" component="h5" variant="subtitle1">
            Use the "*" symbol for auto reply to all incoming messages
          </Typography>

          <TextField
            error={this.state.messageError}
            margin="dense"
            id="name"
            label="Enter auto reply message"
            type="text"
            value={this.state.message}
            onChange={(e) => this.onTextFieldChanged(e, "message")}
            fullWidth
            multiline
            rowsMax="4"
          />


        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.addNewAutoReply()} color="primary">
            {this.state.submitButtonLabel}
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default AutoReplyDetailsModal;
