import React, {Component} from 'react';

import '../common/Common.css';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import CircularProgress from '@material-ui/core/CircularProgress';

class RemoveModal extends Component {

  constructor(props) {
    super(props);
    this.state = {loading: false}
  }

  remove() {
    this.setState({loading: true});
    this.props.remove();
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.closeApplicationRemoveModal}
        aria-labelledby="form-dialog-title">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.props.title}</DialogTitle>

        <DialogContent>

          <DialogContentText>
            {this.props.message}
          </DialogContentText>

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.close()} color="primary">
            Cancel
          </Button>
          <Button disabled={this.state.loading} onClick={(e) => this.remove()} color="primary">
            Remove
          </Button>

          {this.state.loading && <CircularProgress className="custom-remove-progress"/>}

        </DialogActions>
      </Dialog>
    )
  }
}

export default RemoveModal;
