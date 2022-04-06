import React, {Component} from 'react';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import CampaignsAPI from '../campaigns/CampaignsAPI';

class CampaignErrorsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      id: props.selectedCampaign.id,
      title: 'Campaign Errors Lists',
      submitButtonLabel: 'Reset last error message',
      errors: [],
      loading: false
    }

    this.updateErrors = this.updateErrors.bind(this);
  }

  componentDidMount() {
    this.updateErrors();
  }

  updateErrors() {
    const request = {
      id: this.props.selectedCampaign.id
    };

    CampaignsAPI.getCampaignErrors(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          errors: response.data
        });
      }
    });
  }

  resetLastCampaignError() {
    const campaign = {
      id: this.props.selectedCampaign.id
    }

    CampaignsAPI.resetLastCampaignError(campaign,
    response => {
      if (response.status === 'OK') {
        this.props.handleClose(true);
      }
    });
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-lists-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          {this.state.errors.length > 0 &&
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Phone</TableCell>
                  <TableCell>Error</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.errors.map(row => (
                  <TableRow key={row.id}>
                    <TableCell>{this.prettyDate(row.date)}</TableCell>
                    <TableCell>+{row.phone}</TableCell>
                    <TableCell>{row.error}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          }

          {this.state.errors.length === 0 && <span>There are no errors. Everything is OK.</span>}

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default CampaignErrorsModal;
